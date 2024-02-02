# CD12352 - Infrastructure as Code Project Solution

# [YOUR NAME HERE]

## Spin up instructions

###To spin up network infra
./deploy.sh deploy udagram-network network.yml network-parameters.json
###To spin up application infra
./deploy.sh deploy udagram-app udagram.yml udagram-parameters.json

## Tear down instructions

###To tear down network infra
./deploy.sh delete udagram-network
###To tear down application infra
./deploy.sh delete udagram-app

## Other considerations

WEBSITE URL: http://udagra-loadb-rmkolg0gqz2c-2028189434.us-east-1.elb.amazonaws.com/
