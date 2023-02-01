from django.db import models


# Create your models here.
class Product(models.Model):
    pd_id = models.AutoField(primary_key=True)
    product_name = models.CharField(max_length=100, null=False)
    image = models.ImageField(upload_to='products')
    price = models.IntegerField(null=False)
    product_id = models.IntegerField(null=False,unique=True)
    category = models.CharField(max_length=200, null=False)
    store_id = models.IntegerField(null=False)
    quantity = models.IntegerField(default=0)
    description = models.TextField(max_length=200, blank=True)

    def __str__(self):
        return self.product_name
