from django.urls import path

from . import views

urlpatterns = [
    path('addcart', views.addcart, name='addcart'),
    path('addtocart', views.addtocart, name='addtocart'),
    path('getcart', views.getcart, name='getcart'),
    path('getfromcart', views.getfromcart, name='getfromcart'),
    path('deletecart', views.deletecart, name='deletecart'),
    path('orders', views.orders, name='orders'),
    path('getmyorders', views.getmyorders, name='getmyoredrs'),
]
