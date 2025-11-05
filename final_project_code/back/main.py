import requests
from flask import Flask
from flask import request as flask_requests
from flask import render_template
import os
import flask
import json
import socket

app = Flask(__name__)

yandex_key = os.getenv('YANDEX_API')
google_key = os.getenv('GOOGLE_API')
abus_key = os.getenv('ABUS_API')


link_dict = {'yandex': "https://sba.yandex.net/v4/threatMatches:find?key=",
             'google': "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=",
             'abuse': "https://api.abuseipdb.com/api/v2/check"
}

def check_url_safety(api_key: str, url: str, api_link: str, client_version: str):
    api_url = api_link + api_key

    payload = {
        "client": {
            "clientId": "user",
            "clientVersion": f"{client_version}"
        },
        "threatInfo": {
            "threatTypes": ["MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE"],
            "platformTypes": ["ANY_PLATFORM"],
            "threatEntryTypes": ["URL"],
            "threatEntries": [{"url": url}]
        }
    }
    
    response = requests.post(api_url, json=payload)
    
    if response.status_code == 200:
        result = response.json()
        if "matches" in result:
            return ("unsafe")
        else:
            return ("safe")

    else:
        return ("error")
    

def get_ip_by_url(url):
    domain = url.replace('https://', '').replace('http://', '').split('/')[0]
    try:
        ip = socket.gethostbyname(domain)
        return ip
    except socket.gaierror as e:
        return "error"
    
def check_url_safety_abuseipdb(api_key: str, link: str, api_link: str):
    querystring = {
        'ipAddress': link,
        'maxAgeInDays': '90'
    }

    headers = {
        'Accept': 'application/json',
        'Key': api_key
    }

    response = requests.request(method='GET', url=api_link, headers=headers, params=querystring)
    decodedResponse = json.loads(response.text)

    total_reports = decodedResponse.get('data', {}).get('totalReports')
    if total_reports is not None and total_reports > 0:
        return "unsafe"
    else:
        return "safe"
    

check_summary = []

@app.route('/google_api')
def check_url_google():
    global check_summary
    link = flask.requests.args.get('link')
    r = check_url_safety(google_key, link, link_dict['google'], '1.5.2')
    check_summary.append(r)
    return r

@app.route('/yandex_api')
def yandex_api_function():
    global check_summary
    link = flask_requests.args.get('link')
    r = check_url_safety(yandex_key, link, link_dict['yandex'], '{1.1.1}')
    check_summary.append(r)
    return r

@app.route('/abuseipdb_api')
def abuseipdb_api_function():
    global check_summary
    link = flask_requests.args.get('link')
    r = check_url_safety_abuseipdb(abus_key, get_ip_by_url(link), link_dict['abuse'])
    check_summary.append(r)
    return r

@app.route('/summary')
def summary():
    global check_summary
    safe_count = check_summary.count('safe')
    if (safe_count >= 2):
        check_summary = []
        return 'safe'
    else:
        check_summary = []
        return 'unsafe'
    

@app.route('/')
def render_user_guide():
    return render_template('main.html') #Выбор раздела пользовательского руководства

@app.route('/app_main_menu_guide')
def render_main_menu_guide():
    return render_template('app_main_menu.html') #Руководство пользователя по главному меню приложения

@app.route('/app_settings_menu_guide')
def render_settings_menu_guide():
    return render_template('app_settings_menu.html') #Руководство пользователя по меню настроек

@app.route('/app_generate_menu_guide')
def render_generate_menu_guide():
    return render_template('app_generate_menu.html') # Руководство пользователя по меню генерации QR-кода

@app.route('/app_linkcheck_menu_guide')
def render_linkcheck_menu_guide():
    return render_template('app_linkcheck_menu.html') # Руководство пользователя по меню проверки 

if __name__ == '__main__':
    app.run(host = '0.0.0.0', port=2034)
