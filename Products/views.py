import json

import pymongo
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt, csrf_protect
from django.shortcuts import render
from django.http import HttpResponse
from django.core.files.storage import default_storage
from django.core.files.base import ContentFile
import bson

from Products.models import Product

# Create your views here.


client = pymongo.MongoClient('mongodb://localhost:27017')
dbname = client['D2CAPP']

SuccessResponse = {"status": "ok", "errorCode": 0, "message": "None"}
ErrorResponse = {"status": 'error', "errorCode": -1, "message": "failed"}


@csrf_exempt
def addproduct(request):
    if request.method == 'POST':
        collection = dbname["Products_product"]
        product_name = request.POST.get('product_name')
        image = request.FILES.get('image')
        price = request.POST.get('price')
        product_id = request.POST.get('product_id')
        category = request.POST.get('category')
        store_id = request.POST.get('store_id')
        quantity = request.POST.get('quantity')
        description = request.POST.get('description')
        isExists = collection.find_one({'product_id': int(product_id)})
        if isExists:
            ErrorResponse["message"] = "Product Already Added"
            return JsonResponse(ErrorResponse, safe=False)
        else:
            product = Product(product_name=product_name, image=image, price=price, description=description,
                              product_id=product_id,
                              category=category, store_id=store_id, quantity=quantity)
            product.save()
            SuccessResponse["message"] = "Product Registerd Successfully"
            return JsonResponse(SuccessResponse, safe=False)
    return JsonResponse(ErrorResponse, safe=False)


@csrf_exempt
def getproduct(request):
    data = []
    if request.GET:
        Query = request.GET["p_id"]
        product_data = dbname['Products_product'].find_one({"p_id": int(Query)}, {'_id': 0})
        data.append(product_data)
        SuccessResponse["message"] = data
        return JsonResponse(SuccessResponse, safe=False)
    else:
        print("Product")
        for x in dbname["Products_product"].find({}, {'_id': 0}):
            data.append(x)
            SuccessResponse["message"] = data
        return JsonResponse(SuccessResponse)


@csrf_exempt
def updateproduct(request):
    if request.method == 'POST':
        product_data = request.POST
        collection = dbname["Products_product"]
        isExists = collection.find_one({'item_no': product_data['item_no']})
        print(isExists)
        if isExists:
            print("succcessfully created")
            collection.update({'item_no': product_data['item_no']}, {'$set': {'image': request.FILES['image']}})
            SuccessResponse["message"] = "Product Updated Successfully"
            return JsonResponse(SuccessResponse, safe=False)
        else:
            ErrorResponse["message"] = "Product Not Found"
            return JsonResponse(ErrorResponse, safe=False)
    return JsonResponse(SuccessResponse, safe=False)


@csrf_exempt
def deleteproduct(request):
    if request.method == 'POST':
        product_data = json.loads(request.body)
        collection = dbname["Products_product"]
        isExists = collection.find_one({'item_no': product_data['item_no']})
        if isExists:
            collection.delete_one({'item_no': product_data['item_no']})
            SuccessResponse["message"] = "Product Deleted Successfully"
            return JsonResponse(SuccessResponse)
        else:
            ErrorResponse["message"] = "Product Not Found"
            return JsonResponse(ErrorResponse)
