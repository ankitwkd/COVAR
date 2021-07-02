#COVAR
##Covid Vaccine Registration Android Application

###Developers:
1. Ankit Pattanayak 2. Durgesh Valecha 

Glossary:
1. Introduction.
2. Basic User Interaction and flow description. 
3. External APIs Used
4. Features based on Utils used 

**1. Introduction:**
COVAR is a Covid Vaccine Registration Android application intended to remind users before their second vaccine schedule and see their covid vaccine history in a downloadable pdf format. 
COVAR will, therefore, work as a personal assistant to the users.

**2. Basic User Interaction and Flow description.**
As soon as an application is launched, a splash screen is displayed and from there a user’s dashboard is launched if he had logged in earlier or else a login screen is displayed.
Once a user logs in, user dashboard appears and from there a user can fill up the vaccine registration form, change password, change profile, or view the help, about pages. User can also logout from the dashboard view which will take him back to the login page.
Once the vaccine registration form is filled up, a notification is pushed to the user immediately and also before the second vaccine schedule.

3. External API’s used.(UI and Backend)
a. UI.
Name: Google’s Material View Library
Version: 1.0.0
Description: Implementing Google’s UI guidelines, available through central api library.
b. Firebase authentication
c. Firebase realtime database
  Name: com.google.firebase:firebase-auth
Version: 28.2.0
Description: Implementing email‐passsword based authentication throughsingleton class FirebaseAuth
 Name: com.google.firebase:firebase-database
 Version: 28.2.0
Description: Implementing json‐tree based database through singleton class Firebase DatabaseReference. Using a POJO class for user details.
 d. Awesome validation
  Name: com.basgeekball:awesome-validation:4.3
 Version: 4.3
Description: Implementing validation checks for fields based on regex checks. Used TextInputLayout version of the provided API for validation.

4. Features used along with corresponding activities/fragments.
      a. Firebase authentication API (Login/SignUp/ChangePassword)
      b. Firebase realtime database (SignUp/ChangeProfile/VaccineDetails)
      c. Awesome validation — TextInputLayout method with
      regex(SignUp/ChangeProfile/ChangePassword)
      d. PDF creation using Canvas/Paint with Ext. Storage Permission
      e. Notification feature using BroadcastReceiver+AlarmManager(After 1st Vaccine)
      f. CardView for the dashboard(Information of user in dashboard)
      g. NavDrawer for the fragment based navigation
      h. Animations in transitions
      i. Modularized code base
      j. Material theme for UI.
      k. Supporting portrait/landscape orientation of phones/tablets.
      l. Use of firebase supports real‐time tracking of app‐usage as analytics from firebase
      console. Also, real‐time tracking of database and authenticated user‐base.
      
      
     [embed]https://github.com/ankitwkd/COVAR/blob/my-app/app/release/COVAR_DOC.pdf[/embed]
 
