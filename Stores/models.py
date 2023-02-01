from django.db import models


# Create your models here.
class Store(models.Model):
    store_id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=100, null=False)
    city = models.CharField(max_length=100, null=False)
    land_mark = models.CharField(max_length=200)
    building_no = models.IntegerField(null=False)
    license_no = models.CharField(max_length=200)
    owner_name = models.CharField(max_length=200)
    address = models.CharField(max_length=100, null=False)
    pincode = models.IntegerField(null=False)
    phone = models.IntegerField(null=False)
    email = models.EmailField(null=False)

    def __str__(self):
        return self.name
