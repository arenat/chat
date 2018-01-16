from flask import Flask, jsonify, make_response
from flask import request
import json
import ConfigParser

app = Flask(__name__)
in_memory_db = {}
room_history_limit = 5
room_list = []

@app.route('/add_new_message', methods=['POST'])
def create_message():
    data = json.loads(json.dumps(request.json))
    user_name = data["user"]
    user_room = data["room"]
    user_message = data["message"]
    db_entry = {"name":user_name, "message":user_message}
    if user_room in in_memory_db:
        room_messages = in_memory_db[user_room]
        print len(room_messages)
        print room_history_limit
        if len(room_messages) >= int(room_history_limit):
            del room_messages[0]
        room_messages.append(db_entry)
    else:
        in_memory_db[user_room] = [db_entry]
    return jsonify({'result': 'added'}), 200

@app.route('/get_all_messages', methods = ['GET'])
def get_all_messages():
    room = request.args.get('room')
    try:
        resp = json.dumps(in_memory_db[room])
    except KeyError:
        resp = jsonify([])
    return resp, 200

@app.route('/get_room_list')
def get_room_list():
    return json.dumps(room_list), 200

if __name__ == '__main__':
     configParser = ConfigParser.RawConfigParser()   
     configFilePath = 'config.history'
     configParser.read(configFilePath)
     rooml = configParser.get('server', 'room_list')
     host = configParser.get('server', 'server_host')
     port =  configParser.get('server', 'server_port')
     room_history_limit = configParser.get('server', 'room_history_limit')
     room_list = rooml.split(",")
     app.run(host=host, port=int(port), debug=True)
