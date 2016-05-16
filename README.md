# Credit Card Transaction Monitor Mobile App

Mobile application designed to be used with the CreditCardTransactionMonitor demo. 
This mobile application demonstrates how the connected platform allows the customer to provide feedback as to whether the account was correctly disbaled due to suspected fraud. 
If the suspected tranaction is legitimate, then the customer is able to unlock the account with the push of a button.

When the Credit Card Transaction Monitor application detects a fraudulent transaction, the
Customer Service Representative may decide to lock the customer account to prevent further
fraud. When a customer's account is locked, a notification is also sent to the customer 
via the mobile app. 

The mobile app receives notifications via Google Cloud Messaging platform. The customer
can respond to the notification to confirm fraud or mark the transaction as legitimate and
unlock the account. The mobile app sends the customer response back to the Connected Platform 
via Amazon SQS.

In order for the app to work with the Credit Fraud Detection Demo, a Google Cloud Platform
and an Amazon Cloud account is required. These are free to setup and use as long as the number
of messages is kept below the free usage threshold. 

# Create Google Cloud Account

Browse to https://cloud.google.com and click on My Console. Sign in with Google Account.

In the upper right of the screen, click on API Project --> Create new project.

Once project is created, you should be at the Dashboard. If not, click on Dashboard in the
right hand pange. The dashboard should have a section called: 

Project: API Project

ID: api-project-555555555

Take note of the number following api-project: , this is your Authorized Entity. You will need
this number to connect the app to Google Cloud Messaging platform.

The Dashboard should also have a section called:

Use Google APIs

Enable and Manage APIs

Click on Enable and Manage APIs --> In the Left Pane: Click Credentials --> Create Credential --> API Key --> Browser Key.

Take note of the Browser Key as the App will need this credential to open a map.

In the Left Pane: Click Overview --> Google Cloud Messaging --> Enable
In the Left Pane: Click Overview --> Google Maps Android API --> Enable

# Create Amazon Cloud Account

Browse to https://console.aws.amazon.com. Click "I am a new User" and follow instructions
to create an account. Once account is created, log in to https://console.aws.amazon.com.

Click on Services --> SQS.

Click Create New Queue. Enter Queue Name, leave defaults as is, click Create Queue.

Click on the Queue line item in the list. Click on Details.

Take note of the Queue URL, you will need this for the app to respond to fraud notification.

Click on Permissions --> Select Allow --> Click Everybody --> Click Actions --> 

-Select SQS:ReceiveMessage
-SQS:DeleteMessage
-SQS:SendMessage

NOTE: AMAZON PROVIDES A FREE TIER OF SERVICES AS LONG AS NUMBER OF MESSAGES ARE KEPT UNDER
A CERTAIN NUMBER. THE DEMO WILL NOT COME ANY WHERE NEAR THIS LIMIT AS A SINGLE MESSAGE IS 
SENT ONLY WHEN A CUSTOMER RESPONDS TO A CONFIRMED FRAUD ALERT SENT MANUALLY BY THE REPRESENTATIVE.
HOWEVER, AS A BEST PRACTICE, ALWAYS SET THE PERMISSIONS TO "DENY ALL" WHEN DEMO IS NOT IN USE.

# Compile Mobile Application

Download and install Android Studio

git clone https://github.com/vakshorton/CreditCardTransactionMonitorMobileApp.git

From Android Studio, open project, point tot he location where git cloned the repo.

When the project opens with the following folder structure:

-App
--manifest
--java
--res

Under the java folder, browse to:

app-->java-->com.hortonworks.iot.financial.customerapp-->Constants:

public static String customerValidationQueueURL = "YOUR AMAZON SQS QUEUE URL";
public static String authorizedEntity = "YOUR GOOGLE API PROJECT NUMBER";

Under the res folder, browse to:

app-->res-->values-->google_maps_api.xml (debug):

<string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">
ENTER YOUR GOOGLE BROWSER KEY CREDENTIAL HERE
</string>

From the File menu, select Save All

From the Build menu, select Build APK

Once build completes, the event log should show a link called "Reveal in Finder".

Click Reveal in Finder to find the APK (installation package for mobile device)

You can now copy and install the app on a mobile device or run a simulation from the studio.

