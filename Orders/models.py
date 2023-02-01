from django.db import models

from Agents.models import Agent
from Customers.models import Customer
from Products.models import Product
from Stores.models import Store


# Create your models here.
class Cart(models.Model):
    cart_id = models.AutoField(primary_key=True)
    agent_id = models.IntegerField()
    quantity = models.IntegerField()
    store_id = models.IntegerField()
    product_id = models.IntegerField()
    customer_id = models.IntegerField()
    Date = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.cart_id


class Order(models.Model):
    order_id = models.AutoField(primary_key=True)
    date = models.DateTimeField(auto_now_add=True)
    agent_id = models.ForeignKey(Agent, on_delete=models.CASCADE)
    customer_id = models.ForeignKey(Customer, on_delete=models.CASCADE)
    store_id = models.ForeignKey(Store, on_delete=models.CASCADE)
    quantity = models.ForeignKey(Product, on_delete=models.CASCADE)
    cart = models.ForeignKey(Cart, on_delete=models.CASCADE)
    ordered_by = models.CharField(max_length=200)
    shipping_address = models.CharField(max_length=200)
    phone = models.IntegerField()
    email = models.EmailField(max_length=200)


def __str__(self):
    return self.agent_id
