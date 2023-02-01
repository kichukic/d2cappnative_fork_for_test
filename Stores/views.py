import json

import pymongo
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt

from Stores.models import Store

# Create your views here.


client = pymongo.MongoClient('mongodb://localhost:27017')
dbname = client['D2CAPP']

SuccessResponse = {"status": "ok", "errorCode": 0, "message": "None"}
ErrorResponse = {"status": 'error', "errorCode": -1, "message": "failed"}


@csrf_exempt
def addstore(request):
    if request.method == 'POST':
        store_data = json.loads(request.body)
        collection = dbname["Stores_store"]
        name = store_data['name']
        city = store_data['city']
        land_mark = store_data['land_mark']
        building_no = store_data['building_no']
        license_no = store_data['license_no']
        owner_name = store_data['owner_name']
        address = store_data['address']
        pincode = store_data['pincode']
        phone = store_data['phone']
        email = store_data['email']
        isExists = collection.find_one({'phone': store_data['phone']})
        if isExists:
            ErrorResponse["message"] = "Store already Registerd"
            return JsonResponse(ErrorResponse)
        else:
            store = Store(name=name, city=city, land_mark=land_mark, building_no=building_no,
                          license_no=license_no, owner_name=owner_name, address=address, pincode=pincode, phone=phone,
                          email=email)
            store.save()
            SuccessResponse["message"] = "Store Registerd Successfully"
            return JsonResponse(SuccessResponse)
    return JsonResponse(ErrorResponse)


@csrf_exempt
def getstore(request):
    data = []
    if request.GET:
        Query = request.GET["phone"]
        storedata = dbname['Stores_store'].find_one({"phone": int(Query)}, {'_id': 0})
        data.append(storedata)
        SuccessResponse["message"] = data
        return JsonResponse(SuccessResponse, safe=False)
    else:
        for x in dbname["Stores_store"].find({}, {'_id': 0}):
            data.append(x)
            SuccessResponse["message"] = data
        return JsonResponse(SuccessResponse, safe=False)


@csrf_exempt
def updatestore(request):
    if request.method == 'POST':
        store_data = json.loads(request.body)
        collection = dbname["Stores_store"]
        isExists = collection.find_one({'phone': store_data['phone']})
        if isExists:
            collection.update_one({'phone': store_data['phone']}, {'$set': {'name': store_data['name']}},)
            collection.update_one({'phone': store_data['phone']}, {'$set': {'city': store_data['city']}},)
            collection.update_one({'phone': store_data['phone']}, {'$set': {'land_mark': store_data['land_mark']}},)
            collection.update_one({'phone': store_data['phone']}, {'$set': {'building_no': store_data['building_no']}},)
            collection.update_one({'phone': store_data['phone']}, {'$set': {'license_no': store_data['license_no']}},)
            collection.update_one({'phone': store_data['phone']}, {'$set': {'owner_name': store_data['owner_name']}},)
            collection.update_one({'phone': store_data['phone']}, {'$set': {'address': store_data['address']}}, )
            collection.update_one({'phone': store_data['phone']}, {'$set': {'pincode': store_data['pincode']}}, )
            collection.update_one({'phone': store_data['phone']}, {'$set': {'phone': store_data['phone']}},)
            collection.update_one({'phone': store_data['phone']}, {'$set': {'email': store_data ['email']}},)
            SuccessResponse["message"] = "Store Updated Successfully"
            return JsonResponse(SuccessResponse)
        else:
            ErrorResponse["message"] = "No Store Exists"
            return JsonResponse(ErrorResponse)


@csrf_exempt
def deletestore(request):
    if request.method == 'POST':
        store_data = json.loads(request.body)
        collection = dbname["Stores_store"]
        isExists = collection.find_one({'id': store_data['id']})
        if isExists:
            collection.delete_one({'id': store_data['id']})
            SuccessResponse["message"] = "Store Deleted Successfully"
            return JsonResponse(SuccessResponse)
        else:
            ErrorResponse["message"] = "No Store Exists "
            return JsonResponse(ErrorResponse)
