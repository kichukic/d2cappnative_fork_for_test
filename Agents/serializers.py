from rest_framework import serializers
from django.contrib.auth import get_user_model

Agent = get_user_model()


class AgentRegister(serializers.ModelSerializer):

    class Meta:
        model = Agent
        fields = ["username", "password", "email", "pincode", "phone","address"]

    def save(self):
        reg = Agent(
            email=self.validated_data['email'],
            username=self.validated_data['username'],
            pincode=self.validated_data['pincode'],
            phone=self.validated_data['phone'],
            address=self.validated_data['address']
        )
        password = self.validated_data['password']
        if password != password:
            raise serializers.ValidationError({'password': 'password does not match'})
        reg.set_password(password)
        reg.save()
        return reg


class AgentDataSerializer(serializers.ModelSerializer):
    class Meta:
        model = Agent
        fields = ['username', 'email', 'phone', 'pincode']
