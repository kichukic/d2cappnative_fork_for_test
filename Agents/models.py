from django.contrib.auth.models import AbstractUser, BaseUserManager
from django.db import models


# Create your models here.
class AgentManager(BaseUserManager):
    """Define a model manager for User model with no username field."""

    def _create_user(self, phone, password=None, **extra_fields):
        """Create and save a User with the given phone and password."""
        if not phone:
            raise ValueError('The given phone must be set')
        user = self.model(phone=phone, **extra_fields)
        user.set_password(password)
        user.save(using=self._db)
        return user

    def create_user(self, phone, password=None, **extra_fields):
        extra_fields.setdefault('is_staff', False)
        extra_fields.setdefault('is_superuser', False)
        return self._create_user(phone, password, **extra_fields)

    def create_superuser(self, phone, password=None, **extra_fields):
        """Create and save a SuperUser with the given phone and password."""
        extra_fields.setdefault('is_staff', True)
        extra_fields.setdefault('is_superuser', True)

        if extra_fields.get('is_staff') is not True:
            raise ValueError('Superuser must have is_staff=True.')
        if extra_fields.get('is_superuser') is not True:
            raise ValueError('Superuser must have is_superuser=True.')

        return self._create_user(phone, password, **extra_fields)


class Agent(AbstractUser):
    agent_id = models.AutoField(primary_key=True)
    username = models.CharField(max_length=150, null=False, unique=True)
    address = models.CharField(max_length=100, null=False)
    pincode = models.IntegerField(null=False)
    phone = models.CharField(max_length=12, unique=True, verbose_name='Phone Number', blank=False,
                             help_text='Enter 10 digits phone number')
    email = models.EmailField(max_length=200)
    password = models.CharField(max_length=200)
    cart = models.TextField(max_length=200)
    order_id = models.TextField(max_length=200)

    first_name = None
    last_name = None
    is_superuser = None
    is_staff = None

    USERNAME_FIELD = 'username'
    REQUIRED_FIELDS = []

    objects = AgentManager()

    def save(self, *args, **kwargs):
        if not self.pk:
            self.name = self.username
            return super().save(*args, **kwargs)
