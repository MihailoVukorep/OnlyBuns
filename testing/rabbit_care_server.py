from flask import Flask, jsonify

app = Flask(__name__)

# Lokacije za brigu o zečevima
locations = [
    {
        "url": "https://127.0.0.1/5000/1",
        "name": "Zeka Shelter Novi Sad",
        "coordinates": "45.250117203307305,19.80045318603516",
        "type": "SHELTER"
    },
    {
        "url": "https://127.0.0.1/5000/2",
        "name": "Veterinar Kamenica",
        "coordinates": "45.220500773291725,19.846458435058597",
        "type": "VETERINARIAN"
    },
    {
        "url": "https://127.0.0.1/5000/3",
        "name": "Zeke Petrovaradin",
        "coordinates": "45.23911861682938,19.882164001464847",
        "type": "SHELTER"
    }
]

@app.route('/locations', methods=['GET'])
def get_locations():
    return jsonify(locations)

if __name__ == '__main__':
    app.run(port=5000)

