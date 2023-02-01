import json

import pymongo
from django.contrib.auth import authenticate
from django.http import HttpResponse, JsonResponse
from django.views.decorators.csrf import csrf_exempt
from rest_framework.authtoken.models import Token
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework.views import APIView

from Agents.models import Agent
from Agents.serializers import AgentRegister

# Create your views here.


client = pymongo.MongoClient('mongodb://localhost:27017')
dbname = client['D2CAPP']

SuccessResponse = {"status": "ok", "errorCode": 0, "message": "None"}
ErrorResponse = {"status": 'error', "errorCode": -1, "message": "failed"}


def index(request):
    return HttpResponse("Welcome to D2CAPP")


account = dbname["Agents_agent"]


class register(APIView):

    def post(self, request, format=None):
        serializer = AgentRegister(data=request.data)
        data = {}
        if serializer.is_valid():
            account = serializer.save()
            data['response'] = "success"
            data['user_id'] = account.agent_id
            token, create = Token.objects.get_or_create(user=account)
            data['token'] = token.key
            data['message'] = "user created Successfully"
        else:
            data = serializer.errors
        return JsonResponse(data)


class CustomAuthToken(ObtainAuthToken):
    def post(self, request, *args, **kwargs):
        serializer = self.serializer_class(data=request.data,
                                           context={'request': request})
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data['user']
        token, created = Token.objects.get_or_create(user=user)
        return Response({
            'token': token.key,
            'response': "success",
            'message': 'agent login successfull'
        })


class Getagent(APIView):
    permission_classes = (IsAuthenticated,)

    def get(self, request):
        content = {'agent': str(request.user), 'agent_id': str(request.user.agent_id), 'phone': str(request.user.phone)}
        return Response(content)


# @csrf_exempt
# def addagent(request):
#     if request.method == 'POST':
#         agent_data = json.loads(request.body)
#         name = agent_data['name']
#         email = agent_data['email']
#         phone = agent_data['phone']
#         password = agent_data['password']
#         address = agent_data['address']
#         pincode = agent_data['pincode']
#
#         collection = dbname["Agents_agent"]
#
#         if collection.find_one({"phone": agent_data["phone"]}):
#             ErrorResponse["message"] = 'Agent Already Exists'
#             return JsonResponse(ErrorResponse, safe=False)
#
#         elif collection.find_one({"email": agent_data["email"]}):
#             ErrorResponse["message"] = 'Email Already Exists'
#             return JsonResponse(ErrorResponse, safe=False)
#
#         elif password != password:
#             ErrorResponse["message"] = 'Password not Match!'
#             return JsonResponse(ErrorResponse, safe=False)
#
#         else:
#             myagent = Agent(name=name, email=email, phone=phone,
#                             password=password, address=address, pincode=pincode)
#             myagent.save()
#             SuccessResponse["message"] = 'Agent created successfully '
#             return JsonResponse(SuccessResponse, safe=False)
#
#     return JsonResponse(ErrorResponse, safe=False)
#
#
# @csrf_exempt
# def login(request):
#     if request.method == 'POST':
#         agent_data = json.loads(request.body)
#         phone = agent_data['phone']
#         password = agent_data['password']
#         user = authenticate(request, phone=phone, password=password)
#
#         collection = dbname["Agents_agent"]
#
#         if user is not None:
#             data = {}
#             Response = {}
#             data['username'] = user.get_username()
#             Response["message"] = 'Agent logged in  successfully'
#             Response['data'] = data
#             return JsonResponse(Response, safe=False)
#         else:
#             ErrorResponse["message"] = 'invalid username or password'
#             return JsonResponse(ErrorResponse, safe=False)
#     return JsonResponse(ErrorResponse)


# @csrf_exempt
# def addagent(request):
#     if request.method == 'POST':
#         agent_data = json.loads(request.body)
#         collection = dbname["Agents_agent"]
#         name = agent_data['name']
#         address = agent_data['address']
#         pin = agent_data['pin']
#         phone = agent_data['phone']
#         isExists = collection.find_one({'phone': agent_data['phone']})
#         if isExists:
#             ErrorResponse["message"] = "Agent Already Registerd"
#             return JsonResponse(ErrorResponse)
#         else:
#             agent = Agent(name=name, address=address, pin=pin, phone=phone)
#             agent.save()
#             SuccessResponse["message"] = "Agent Registerd Successfully"
#             return JsonResponse(SuccessResponse)
#     return JsonResponse(ErrorResponse)
#

@csrf_exempt
class getagent(APIView):
    permission_classes = (IsAuthenticated,)

    def getagents(request):
        data = []
        if request.GET:
            Query = request.GET["phone"]
            agentdata = dbname['Agents_agent'].find_one({"phone": int(Query)}, {'_id': 0})
            data.append(agentdata)
            SuccessResponse["message"] = data
            return JsonResponse(SuccessResponse)
        else:
            for x in dbname["Agents_agent"].find({}, {'_id': 0}):
                data.append(x)
                SuccessResponse["message"] = data
            return JsonResponse(SuccessResponse)

# @csrf_exempt
# def updateagent(request):
#     if request.method == 'POST':
#         agent_data = json.loads(request.body)
#         collection = dbname["Agents_agent"]
#         isExists = collection.find_one({'id': agent_data['id']})
#         if isExists:
#             collection.update_one({'id': agent_data['id']}, {'$set': {'name': agent_data['name']}}, )
#             collection.update_one({'id': agent_data['id']}, {'$set': {'address': agent_data['address']}}, )
#             collection.update_one({'id': agent_data['id']}, {'$set': {'pin': agent_data['pin']}}),
#             collection.update_one({'id': agent_data['id']}, {'$set': {'phone': agent_data['phone']}}, )
#             SuccessResponse["message"] = "Agent Updated Successfully"
#             return JsonResponse(SuccessResponse)
#         else:
#             ErrorResponse["message"] = "Agent Not Found"
#             return JsonResponse(ErrorResponse)
#
#
# @csrf_exempt
# def deleteagent(request):
#     if request.method == 'POST':
#         agent_data = json.loads(request.body)
#         collection = dbname["Agents_agent"]
#         isExists = collection.find_one({'id': agent_data['id']})
#         if isExists:
#             collection.delete_one({'id': agent_data['id']})
#             SuccessResponse["message"] = "Agent Deleted Successfully"
#             return JsonResponse(SuccessResponse)
#         else:
#             ErrorResponse["message"] = "Agent Not Found"
#             return JsonResponse(ErrorResponse)
