import json

import pymongo
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt

from Orders.models import Cart, Order

client = pymongo.MongoClient('mongodb://localhost:27017')
dbname = client['D2CAPP']

SuccessResponse = {"status": "ok", "errorCode": 0, "message": "None"}
ErrorResponse = {"status": 'error', "errorCode": -1, "message": "failed"}


# Create your views here.
@csrf_exempt
def addcart(request):
    if request.method == 'POST':
        cart_data = json.loads(request.body)
        print(cart_data)
        collection = dbname["Agents_agent"]
        product_id = cart_data['product_id']
        agent_id = cart_data['agent_id']
        customer_id = cart_data['customer_id']
        store_id = cart_data['store_id']
        quantity = cart_data['quantity']
        isExists = collection.find_one({'agent_id': int(agent_id)})

        if isExists:
            if isExists.get('cart'):
                cart = json.loads(isExists.get('cart'))
            else:
                cart = []
            cart.append({
                "product_id": product_id,
                "store_id": store_id,
                "agent_id": agent_id,
                "customer_id": customer_id,
                "quantity": quantity
            })
            obj = collection.find({'agent_id': int(agent_id)})
            collection.update_one({'agent_id': agent_id}, {'$set': {'cart': json.dumps(cart)}}, )
            SuccessResponse["message"] = "Item  Added in Cart  Successfully"
            return JsonResponse(SuccessResponse)

        else:
            return JsonResponse(ErrorResponse)
    return JsonResponse(ErrorResponse)


@csrf_exempt
def addtocart(request):
    if request.method == 'POST':
        cart_data = json.loads(request.body)
        collection = dbname["Carts_cart"]
        item_no = cart_data['item_no']
        agent_id = cart_data['agent_id']
        user_id = cart_data['user_id']
        store_id = cart_data['store_id']
        quantity = cart_data['quantity']
        isExists = collection.find_one({'item_no': cart_data['item_no']})
        print(isExists)
        if isExists:
            ErrorResponse["message"] = "Item Already In Cart"
            return JsonResponse(ErrorResponse)
        else:
            cart = Cart(item_no=item_no, agent_id=agent_id, user_id=user_id, store_id=store_id, quantity=quantity)
            cart.save()
            SuccessResponse["message"] = "Item  Added to Cart  Successfully"
            return JsonResponse(SuccessResponse)


@csrf_exempt
def getcart(request):
    data = []
    if request.GET:
        Query = request.GET["agent_id"]
        userdata = dbname['Agents_agent'].find_one({"agent_id": int(Query)}, {'_id': 0})
        item_no = int(userdata['cart']['item_no'])
        productdata = dbname['Products_product'].find_one({"item_no": item_no}, {'_id': 0})
        print(productdata)
        data = userdata
        # data.append(userdata['cart'])
        for i in productdata.keys():
            data[i] = productdata[i]
        SuccessResponse["message"] = data
        return JsonResponse(SuccessResponse)
    else:
        for x in dbname["Agents_agent"].find({}, {'_id': 0}):
            del x['password']
            del x['last_login']
            data.append(x)
            SuccessResponse["message"] = data
        return JsonResponse(SuccessResponse)


@csrf_exempt
def getfromcart(request):
    data = []
    if request.GET:
        Query = request.GET["agent_id"]
        userdata = dbname['Carts_cart'].find_one({"agent_id": int(Query)}, {'_id': 0})
        item_no = int(userdata['cart']['item_no'])
        productdata = dbname['Products_product'].find_one({"item_no": item_no}, {'_id': 0})
        print(productdata)
        data = userdata
        # data.append(userdata['cart'])
        for i in productdata.keys():
            data[i] = productdata[i]
        SuccessResponse["message"] = data
        return JsonResponse(SuccessResponse)
    else:
        for x in dbname["Carts_cart"].find({}, {'_id': 0}):
            data.append(x)
            SuccessResponse["message"] = data
        return JsonResponse(SuccessResponse)


@csrf_exempt
def deletecart(request):
    if request.method == 'POST':
        cart_data = json.loads(request.body)
        print(cart_data)
        collection = dbname["Agents_agent"]
        isExists = collection.find_one({'cart': 'product_id'})
        print(isExists)
        if isExists:
            collection.delete_one({'item_no': cart_data['item_no']})
            SuccessResponse["message"] = "Item  Deleted Successfully"
            return JsonResponse(SuccessResponse)
        else:
            ErrorResponse["message"] = "Item  Not Found"
            return JsonResponse(ErrorResponse)


customerdb = dbname["Customers_customer"]
productdb = dbname["Products_product"]

@csrf_exempt
def orders(request):
    if request.method == 'POST':
        checkout = json.loads(request.body)
        collection = dbname["Orders_order"]
        agent_id = checkout['agent_id']
        customer_id = checkout['customer_id']
        product_id = checkout['product_id']
        total_price = checkout['total_price']
        store_id = checkout['store_id']
        quantity = checkout['quantity']
        shipping_address = checkout['shipping_address']
        phone = checkout['phone']
        isExists = productdb.find_one({'product_id': checkout['product_id']})
        print(isExists)
        if isExists:
            if isExists.get('order_id'):
                order_id = json.loads(isExists.get('order_id'))
            else:
                order_id = []
            order_id.append({
                "order_id": order_id,
            })
            obj = customerdb.find({'customer_id': int(customer_id)})
            customerdb.update_one({'customer_id': customer_id}, {'$set': {'order_id': json.dumps(order_id)}}, )
            myorder = Order(agent_id=agent_id, customer_id=customer_id, product_id=product_id, total_price=total_price,
                            store_id=store_id, quantity=quantity, shipping_address=shipping_address, phone=phone, )
            myorder.save()
            SuccessResponse["message"] = "Order Placed Successfully"
            return JsonResponse(SuccessResponse)
        else:
            ErrorResponse["message"] = "Error in Order"
            return JsonResponse(ErrorResponse)


def getmyorders(request):
    data = []
    if request.GET:
        Query = request.GET["order_id"]
        userdata = dbname['Orders_order'].find_one({"order_id": int(Query)}, {'_id': 0})
        data.append(userdata)
        SuccessResponse["message"] = data
        return JsonResponse(SuccessResponse)
    else:
        for x in dbname["Orders_order"].find({}, {'_id': 0}):
            data.append(x)
            SuccessResponse["message"] = data
        return JsonResponse(SuccessResponse)
