from django.urls import path
from django.conf import settings
from django.conf.urls.static import static

from . import views

urlpatterns = [
    path('addproduct', views.addproduct, name='addproduct'),
    path('getproduct', views.getproduct, name='getproduct'),
    path('updateproduct', views.updateproduct, name='updateaproduct'),
    path('deleteproduct', views.deleteproduct, name='deleteproduct'),
]

