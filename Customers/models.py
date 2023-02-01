from django.db import models


# Create your models here.
class Customer(models.Model):
    customer_id = models.AutoField(primary_key=True)
    first_name = models.CharField(max_length=150)
    last_name = models.CharField(max_length=150, null=True)
    house = models.CharField(max_length=200)
    land_mark = models.CharField(max_length=200)
    street_address = models.CharField(max_length=200, null=True,)
    city = models.CharField(max_length=100, null=False)
    state = models.CharField(max_length=100, null=False)
    pincode = models.IntegerField(null=False)
    phone = models.IntegerField(null=False, blank=True, unique=True)
    order_id = models.TextField(max_length=200)

    def save(self, *args, **kwargs):
        if not self.pk:
            self.first_name = self.first_name
            return super().save(*args, **kwargs)
