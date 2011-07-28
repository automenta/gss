import sys
import csv
from geopy import geocoders  

#NOTE: lng,lat are the order used -- not lat,lng

#http://code.google.com/p/geopy/wiki/GettingStarted

#gn = geocoders.GeoNames()  
gn = geocoders.Google('ABQIAAAA3D2mD_qMSK4fmuGtL57T-xR2EfpWGMemlSpBE39E4xKN-iO6rhQajWwblRonRDn-pUKmkXPmjtkK4A')

addresslocs = { }
address = sys.argv[1]
addresslocs[address] = list(gn.geocode(address, exactly_one=False))

if (len(addresslocs[address])==0):
	print 'not found'

place, (lat, lng) = addresslocs[address][0]
print str(lat) + ',' + str(lng) + ',' + place
print


