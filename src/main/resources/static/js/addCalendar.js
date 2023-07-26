let loginid = document.getElementById("loginId");
          const saveid = $("#loginId").val();


          function selectedInvite(){
            var selected = document.querySelector("#calendarType");
            var inputBox = document.querySelector(".hidden-box");
            var colorType = document.querySelector("#calendarColor");

            if(selected.options[selected.selectedIndex].value == "invite"){
              inputBox.style.display="block";
              colorType.disabled= true;
              $("#inviteCode").val("");

            }
            else{
              inputBox.style.display="none";
              colorType.disabled = false;
              $("#inviteCode").val("");
            }
          }

          $(document).ready(function(){
            $("#btnChkInvite").on("click", function(e){
               e.preventDefault();
               $.ajax({
                 url : "/CalendarCategory/findCtgr"
                 ,data : {"ctgrseqn" : $("#inviteCode").val()}
                 ,async : false
                 ,success :function(data){
                   console.log("data : "+data);
                    if(data != ""){
                      $("#txtRes").text("").css("color","green");
                      var codes = data.split(',')
                      for(let i=0; i<codes.length; i++){
                        $("#calendarColor option[value=" +codes[i]+"]").remove();
                      }
                      $("#calendarColor").prop("disabled", false);
                    }
                    else{
                      $("#txtRes").text("유효하지 않은 초대코드입니다.").css("color","red");
                      $("#calendarColor").css('disabled', 'disabled');
                    }
                 },error : function(req,status,err){
                     console.log(req);
                 }
              });
            });
          });