document.addEventListener("DOMContentLoaded", function(){
    //$(".social-login-text").on("click",function(e){
        $.ajax({
            url :"/login/oauth2/google"
            //url:"http://localhost:8080/login/oauth2/google?code=4%2F0Adeu5BUM7ZpZqLnI8V1j0SgbogXxSy9r2nIUFrh5MlvU0WDmB_GEuRR9GPBpL3oPuCcs7A&scope=email+profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+openid&authuser=0&prompt=consent"
            ,type:"get"
            ,data:{}
            ,async:false
            ,success:function(data){
                alert("success");
                console.log("get data : " + data);

                window.localStorage.setItem("accessToken", data.headers.get("access_Token"));
                window.localStorage.setItem("refreshToken", data.headers.get("refresh_Token"));
                window.localStorage.setItem("expiredTime", data.headers.get("expired_Time"));
                
                location.replace("redirect:/Calendar");
            }
            ,error:function(error){
                console.log("err : "+error);
            }
        });
    //});
});


     function setToken(jqXHR){
        let accessToken =  localStorage.getItem('accessToken');
        let expiredTime =  localStorage.getItem('expiredTime');
        let refreshToken =  localStorage.getItem('refreshToken');
        let exTime = expiredTime - new Date();

        // accessToken 만료시간 다 되었을 시 (10s)
        if(exTime <10000){
            jqXHR.setRequestHeader("refresh_token", refreshToken);
        }

        else{
            jqXHR.setRequestHeader("access_token", accessToken);
        }
    }