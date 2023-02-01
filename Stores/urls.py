from django.urls import path

from . import views

urlpatterns = [
    path('addstore', views.addstore, name='addstore'),
    path('getstore', views.getstore, name='getstore'),
    path('updatestore', views.updatestore, name='updatestore'),
    path('deletestore', views.deletestore, name='deletestore'),
]
