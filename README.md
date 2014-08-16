Monitordroid
============

Remote Administration Tool for Android

The goal of this project is to give users the ability to control their Android mobile devices from any web browser. 

Previously it was a major networking challenge to send packets to a device on a 3G/4G data connection due to mobile
firewalls, but Monitordroid navigates around this by using Google's Cloud Messaging API to send remote commands to the
device. 

I own currently own a paid hosting service at http://www.monitordroid.com/ which is a platform for sending commands 
to the device. A Premium Account can currently be purchased for only $9.99 and will allow you to easily connect to your devices
without having to worry about creating a database or doing any PHP coding. However, the Monitordroid mobile application
can still be controlled for free by any GCM-Capable server if the proper settings in the code are changed.
A guide on setting up a GCM-Capable server can be found at http://www.androidhive.info/2012/10/android-push-notifications-using-google-cloud-messaging-gcm-php-and-mysql/


The reason I decided to make Monitordroid open-source is that I believe it has the potential to become a very useful,
powerful tool if talented developers are able to create new features themselves. The networking and basic feature foundation
has been laid and is very stable, giving open-source contributors the freedom to create features for Monitordroid that will
truly allow users to control their devices, anywhere. 
