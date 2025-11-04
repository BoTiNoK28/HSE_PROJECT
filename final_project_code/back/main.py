import requests
from flask import Flask
from flask import request as flask_requests
from flask import render_template
import os
import flask

app = Flask(__name__)

yandex_key = os.getenv('YANDEX_API')
google_key = os.getenv('GOOGLE_API')
abus_key = os.getenv('ABUS_API')


link_dict = {'yandex': "https://sba.yandex.net/v4/threatMatches:find?key=",
             'google': "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=",
             'abuse': ""
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


@app.route('/google_api')
def check_url_google(url):
	link = flask.requests.args.get('link')
	return check_url_safety(google_key, link, link_dict['google'], '1.5.2')

@app.route('/yandex_api')
def yandex_api_function():
    link = flask_requests.args.get('link')
    return check_url_safety(yandex_key, link, link_dict['yandex'], '{1.1.1}')

@app.route('/abuseipdb_api')
def abuseipdb_api_function():
    link = flask_requests.args.get('link')

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
