# Weather API Proxy

Provides an internal REST endpoint for various functions. Currently
 
1. OpenWeatherMap API for current conditions and particularly Sea Level Barometer
2. API Proxy for calls to garage door sensors

I'll probably move the garage doors out to their own service in k8s when I add some additional sensor endpoints that I have cooking.