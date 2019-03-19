node('ec2cloud-maven'){
    stage('checkout'){
        git 'https://github.com/lokeshkamalay/simple-java-maven-app.git'
    }
    stage('Build'){
        sh "mvn clean package"
    }
    stage('awscli'){
        sh '''
        curl -O https://bootstrap.pypa.io/get-pip.py
        sudo python get-pip.py
        sudo pip install awscli
        aws --version
        '''
    }
    stage('Copy to S3'){
        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'aws-ec2-batch2-creds', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
            sh '''
                aws s3 cp target/*.jar s3://lokeshkamalay/
                if [[ $? -eq 0 ]]; then
                    echo "The file is copied successfully" > result.txt
                else:
                    echo "The file is failed to copy" > result.txt
            '''
        }
    }
    stage('Email'){
        sh 'sendmail lokesh.mydilse@gmail.com < result.txt'
    }
}

