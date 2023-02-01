from django.urls import path

from . import views

urlpatterns = [
    path('', views.index, name='index'),
    path('getcustomer', views.getcustomer, name='getcustomer'),
    path('addcustomer', views.addcustomer, name='addcustomer'),
    path('updatecustomer', views.updatecustomer, name='updatecustomer'),
    path('deletecustomer', views.deletecustomer, name='deletecustomer'),

]
