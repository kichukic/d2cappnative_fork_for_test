from django.urls import path
from rest_framework.authtoken.views import obtain_auth_token

from . import views
from .views import CustomAuthToken

urlpatterns = [
    path('', views.index, name='index'),
    path('agentregister', views.register.as_view(), name='agentregister'),
    path('agentlogin', CustomAuthToken.as_view(), name="agentlogin"),
    path('getagent', views.Getagent.as_view(), name="getagent"),
#     path('addagent', views.addagent, name='addagent'),
#     path('login', views.login, name='login'),
#     path('getagent', views.getagent.as_view(), name='getagent'),
#     path('updateagent', views.updateagent, name='updateagent'),
#     path('deleteagent', views.deleteagent, name='deleteagent'),
]
