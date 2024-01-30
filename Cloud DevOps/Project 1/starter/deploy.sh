#!/bin/bash
# Automation script for CloudFormation templates. 
#
# Parameters
#   $1: Execution mode. Valid values: deploy, delete, preview.
#   $2: Name of the cloudformation stack.
#   $3: Name of the template file.
#   $4: Name of the parameters file.
#
# Usage examples:
#   ./deploy.sh deploy udagram network.yml network-parameters.json
#   ./deploy.sh preview udagram network.yml network-parameters.json
#   ./deploy.sh delete udagram
#

# Validate parameters
if [[ $1 != "deploy" && $1 != "delete" && $1 != "preview" ]]; then
    echo "ERROR: Incorrect execution mode. Valid values: deploy, delete, preview." >&2
    exit 1
fi

EXECUTION_MODE=$1
REGION="us-east-1"
STACK_NAME=$2
TEMPLATE_FILE_NAME=$3
PARAMETERS_FILE_NAME=$4

# Execute CloudFormation CLI
if [ $EXECUTION_MODE == "deploy" ]
then
    aws cloudformation deploy \
        --stack-name $STACK_NAME \
        --template-file $TEMPLATE_FILE_NAME \
        --parameter-overrides file://$PARAMETERS_FILE_NAME \
        --capabilities "CAPABILITY_IAM" "CAPABILITY_NAMED_IAM"
        --region=$REGION
fi
if [ $EXECUTION_MODE == "delete" ]
then
    aws cloudformation delete-stack \
        --stack-name $STACK_NAME \
        --region=$REGION
fi
if [ $EXECUTION_MODE == "preview" ]
then
    aws cloudformation deploy \
        --stack-name $STACK_NAME \
        --template-file $TEMPLATE_FILE_NAME \
        --parameter-overrides file://$PARAMETERS_FILE_NAME \
        --no-execute-changeset \
        --region=$REGION 
fi
