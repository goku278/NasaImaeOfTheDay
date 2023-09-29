Nasa Image of The Day Api

Through this api I am fetching images uploaded by NASA in their server every mid-night.

So I have used here caching mechanism, as based on the mid-night timings.

As, consider a scenario where user is trying to obtain image and other relevant data
through thi NASA's api, then there are 2 scenarios - 

1. User trying to fetch data before 12 am - 
   
   1.1- So here once the data are fetched via api once, then using the caching mechanism all these data 
        parameters coming via the api's response are cached in a no-sql based memory, called as - PaperDb

        PaperDb is a no-sql based very fast database, where access and storage occurs very fast.


   1.2- So when user try to open the app once again, then there won't be any api calls hitted to the NASA's 
        image server, instead the cached data will be retrieved and shown on the screen.

2. User trying to obtain data on or after 12 am.

   2.1 - Here the data will be fetched via api call, as on every mid-night NASA uplaods new data in their server.

   2.2 - Then this new data will be stored in the db and then can be used as caching to show the data on screen.

3. User trying to sync or refresh the page

   3.1 - So in this scenario, user will obtain the same data, bu an api call to the server is requested and the same,
         but the data are obtained from the NASA's image server.

4. Incase a video appers instead of the image.

   4.1 - So in this scenario, I have replaced the image screen with a video player screen.

   4.2 - Hence, videos can be played in this application, as well. 

5. API KEY

   5.1 - The APi key for this project is already mentioned in the project, which ever is pushed in the github,
         however, mentioning this api key here in this readme file as well.

   5.2 - Api Key as follows :- 8S3ytGWYw74JNy9f5nVMEQ3sRoMsvgGYYAUWiN1p