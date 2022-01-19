const AWS = require('aws-sdk');
const sts = new AWS.STS({apiVersion: '2011-06-15'});

userId = 123;

const bucketpolicy = {
        "Version": "2012-10-17",
        "Statement": [
          {
            "Sid": "AllowAccessFromQUTAWSAccount",
            "Effect": "Allow",
            "Principal": { "AWS": "arn:aws:iam::901444280953:root" },
            "Action": "s3:*",
            "Resource": "arn:aws:s3:::YOUR_BUCKET_NAME/*"
          }
        ]
      }

const role = {
        RoleArn: 'arn:aws:iam::901444280953:role/user1234',
        Policy: bucketpolicy,
        RoleSessionName: 'webClientRole',
        DurationSeconds: 3600
}

sts.assumeRole(role, (err, data) => {
        console.log({
           accessKeyId: data.Credentials.AccessKeyId,
           secretAccessKey: data.Credentials.SecretAccessKey,
           sessionToken: data.Credentials.SessionToken     
        });
});