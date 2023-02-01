import json

import pymongo
from django.http import HttpResponse, JsonResponse
from django.views.decorators.csrf import csrf_exempt
from Customers.models import Customer

# Create your views here.


client = pymongo.MongoClient('mongodb://localhost:27017')
dbname = client['D2CAPP']

SuccessResponse = {"status": "ok", "errorCode": 0, "message": "None"}
ErrorResponse = {"status": 'error', "errorCode": -1, "message": "failed"}


def index(request):
    return HttpResponse("Hello, world. You're at the polls index.")


@csrf_exempt
def addcustomer(request):
    if request.method == 'POST':
        user_data = json.loads(request.body)
        collection = dbname["Customers_customer"]
        first_name = user_data['first_name']
        last_name = user_data['last_name']
        house = user_data['house']
        land_mark = user_data['land_mark']
        street_address = user_data['street_address']
        city = user_data['city']
        state = user_data['state']
        pincode = user_data['pincode']
        phone = user_data['phone']
        isExists = collection.find_one({'phone': user_data['phone']})
        if isExists:
            ErrorResponse["message"] = "User Already Registerd"
            return JsonResponse(ErrorResponse)
        else:
            user = Customer(first_name=first_name, last_name=last_name, house=house, land_mark=land_mark,
                            street_address=street_address, city=city, state=state, pincode=pincode, phone=phone)
            user.save()
            SuccessResponse["message"] = "User Registerd Successfully"
            return JsonResponse(SuccessResponse)
    return JsonResponse(ErrorResponse)


@csrf_exempt
def getcustomer(request):
    data = []
    if request.GET:
        Query = request.GET["phone"]
        userdata = dbname['Customers_customer'].find_one({"phone": int(Query)}, {'_id': 0})
        data.append(userdata)
        SuccessResponse["message"] = data
        return JsonResponse(SuccessResponse)
    else:
        for x in dbname["Customers_customer"].find({}, {'_id': 0}):
            data.append(x)
            SuccessResponse["message"] = data
        return JsonResponse(SuccessResponse)


@csrf_exempt
def updatecustomer(request):
    if request.method == 'POST':
        store_data = json.loads(request.body)
        collection = dbname["Customers_customer"]
        isExists = collection.find_one({'phone': store_data['phone']})
        if isExists:
            collection.update_one({'phone': store_data['phone']}, {'$set': {'first_name': store_data['first_name']}}, )
            collection.update_one({'phone': store_data['phone']}, {'$set': {'last_name': store_data['last_name']}}, )
            collection.update_one({'phone': store_data['phone']}, {'$set': {'house_no': store_data['house_no']}}, )
            collection.update_one({'phone': store_data['phone']}, {'$set': {'land_mark': store_data['land_mark']}}, )
            collection.update_one({'phone': store_data['phone']},
                                  {'$set': {'street_address': store_data['street_address']}}, )
            collection.update_one({'phone': store_data['phone']}, {'$set': {'city': store_data['city']}}, )
            collection.update_one({'phone': store_data['phone']}, {'$set': {'state': store_data['state']}}, )
            collection.update_one({'phone': store_data['phone']}, {'$set': {'pincode': store_data['pincode']}}, )
            collection.update_one({'phone': store_data['phone']}, {'$set': {'phone': store_data['phone']}}, )
            SuccessResponse["message"] = "Customer Updated Successfully"
            return JsonResponse(SuccessResponse)
        else:
            ErrorResponse["message"] = "Can't  Update Customer "
            return JsonResponse(ErrorResponse)


@csrf_exempt
def deletecustomer(request):
    if request.method == 'POST':
        store_data = json.loads(request.body)
        collection = dbname["Customers_customer"]
        isExists = collection.find_one({'phone': store_data['phone']})
        if isExists:
            collection.delete_one({'phone': store_data['phone']})
            SuccessResponse["message"] = "Customer Deleted Successfully"
            return JsonResponse(SuccessResponse)
        else:
            ErrorResponse["message"] = "Customer Not Found"
            return JsonResponse(ErrorResponse)
