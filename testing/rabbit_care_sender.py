import requests

spring_url = "http://localhost:8080/api/map/locations/send"

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

for loc in locations:
    r = requests.post(spring_url, json=loc)
    print(f"Sent: {loc['name']} -> {r.status_code}")
