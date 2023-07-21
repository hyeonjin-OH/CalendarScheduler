

    document.addEventListener('DOMContentLoaded', function () {
    	  // ajax 처리로 데이터를 로딩 시킨다.
          $(function () {
    	    var request = $.ajax({
    		 type:"get",
    		 url:"/Calendar/schedules"
            });

            request.done(function (data){
              var calendarEl = document.getElementById('calendarBox');
              var calendar = new FullCalendar.Calendar(calendarEl, {
                timeZone: 'UTC',
                navLinks:true,
                initialView: 'dayGridMonth',
                events:JSON.parse(data),
                displayEventTime:true,
                headerToolbar: {
                    start: 'dayGridMonth,timeGridWeek,dayGridDay', // headerToolbar에 버튼을 추가
                    center: 'title',
                    end: 'today prev,next'  // 스페이스-버튼띄움 ,-붙여서 생성
                },/*customButtons: {
                    addEventButton: { // 추가한 버튼 설정
                        text : "일정 추가",  // 버튼 내용
                        click : function(){ // 버튼 클릭 시 이벤트 추가
                            modalSetting();
                            modalDataSetting();
                        }
                    }
                },*/dateClick: function(info) {
                    modalSetting();
                    modalDataSetting(info);
                }
                ,eventClick:function(info){
                  const loginId = document.querySelector("#loginMbrId").value;
                  const eventId = info.event.extendedProps.scdId;
                  const id = info.event.extendedProps.id;

                  if(loginId == eventId)
                  {
                    document.querySelector("#submitEdit").disabled =false;
                    document.querySelector("#submitDel").disabled =false;
                  }
                  else{
                    document.querySelector("#submitEdit").disabled =true;
                    document.querySelector("#submitDel").disabled =true;
                  }

                  modalUpdateSetting();

                    function convertFormToJSON(form) {
                      return $(form)
                        .serializeArray()
                        .reduce(function (json, { name, value }) {

                          json[name] = value;
                          return json;
                        }, {});
                    }

                    $(document).ready(function(){
                        $("#submitEdit").on("click", function(e){
                          var schedule = $("#schedulePop_update");
                          var s =JSON.stringify(convertFormToJSON(schedule));

                          const arr = JSON.parse(s);
                          arr.scdFrtmchk = $("#frtm_C_update").is(":checked").toString();
                          arr.scdTotmchk = $("#totm_C_update").is(":checked").toString();
                          arr.id = id;
                          delete arr._scdTotmchk;
                          delete arr._scdFrtmchk;
                          var schdInfo =JSON.stringify(arr);

                          e.preventDefault();

                          $.ajax({
                            url :"/schedulePop/edit"
                            ,type: "post"
                            ,data: schdInfo
                            ,contentType : 'application/json'
                            ,success:function(data){
                                console.log("success");
                                $("#schedulePop_update")[0].reset();
                                $("#modal.modal-overlay").css('display','none');
                                $(".modal-window-update").css('display','none');
                                $('.close-area').css('display','none');
                                location.reload();
                              }
                          });
                        });
                      });

                      $(document).ready(function(){
                        $("#submitDel").on("click", function(e){
                          var schedule = $("#schedulePop_update");
                          var s =JSON.stringify(convertFormToJSON(schedule));

                          const arr = JSON.parse(s);
                          arr.scdFrtmchk = $("#frtm_C_update").is(":checked").toString();
                          arr.scdTotmchk = $("#totm_C_update").is(":checked").toString();
                          arr.id = id;
                          delete arr._scdTotmchk;
                          delete arr._scdFrtmchk;
                          var schdInfo =JSON.stringify(arr);

                          e.preventDefault();

                          $.ajax({
                            url :"/schedulePop/delete"
                            ,type: "post"
                            ,data: schdInfo
                            ,contentType : 'application/json'
                            ,success:function(data){
                                console.log("success");
                                $("#schedulePop_update")[0].reset();
                                $("#modal.modal-overlay").css('display','none');
                                $(".modal-window-update").css('display','none');
                                $('.close-area').css('display','none');
                                location.reload();
                              }
                          });
                        });
                      });

                    let subj =document.getElementById("subject_update");
                    let cnts =document.getElementById("contents_update");
                    let frdt =document.getElementById("frdt_update");
                    let todt =document.getElementById("todt_update");
                    let frtm =document.getElementById("frtm_update");
                    let totm =document.getElementById("totm_update");

                    subj.value = info.event.title;
                    cnts.value = info.event.extendedProps.description;
                    var yyyymmdd_s = info.event.start.toISOString().slice(0,10);
                    var yyyymmdd_e = info.event.end.toISOString().slice(0,10);
                    var hhmmss_s = info.event.start.toISOString().slice(11,19);
                    var hhmmss_e = info.event.end.toISOString().slice(11,19);

                    frdt.value = yyyymmdd_s;
                    todt.value = yyyymmdd_e;
                    frtm.value = hhmmss_s;
                    totm.value = hhmmss_e;

                    let chkFrtm = document.querySelector("#frtm_C_update");
                    let chkTotm = document.querySelector("#totm_C_update");

                    chkFrtm.addEventListener("click", function(){
                        if(chkFrtm.checked){
                            frtm.disabled = true;
                        }
                        else{
                            frtm.disabled = false;
                        }
                    });

                    chkTotm.addEventListener("click", function(){
                        if(chkTotm.checked){
                            totm.disabled = true;
                        }
                        else{
                            totm.disabled = false;
                        }
                    });
                }
                ,selectable : true
            });
            calendar.render();
            });
            request.fail(function( jqXHR, textStatus ) {
                    alert( "Request failed: " + textStatus );
            });
        });
    });

    function modalSetting(){
        var screen = document.querySelector('#modal.modal-overlay');
        screen.style.display = "flex";

        var modal = document.querySelector('.modal-window');
        modal.style.display = "flex";

        var close = document.querySelector('.close-area');
        close.style.display="flex";
        
        const closeEvent = close.addEventListener("click", e=>{
            modal.style.display="none";
            screen.style.display="none";
            close.style.display="none";
        });

       screen.scroll(function() { return false; });

       screen.addEventListener("click", e=>{
            const eTarget = e.target;
            if(eTarget.classList.contains("modal-overlay")){
                modal.style.display = "none";
                screen.style.display="none";
                close.style.display="none";
            }
       });
    }

    function modalUpdateSetting(){
        var screen = document.querySelector('#modal.modal-overlay');
        screen.style.display = "flex";

        var modal = document.querySelector('.modal-window-update');
        modal.style.display = "flex";

        var close = document.querySelector('.close-area');
        close.style.display="flex";
        
        const closeEvent = close.addEventListener("click", e=>{
            modal.style.display="none";
            screen.style.display="none";
            close.style.display="none";
        });

       screen.scroll(function() { return false; });

       screen.addEventListener("click", e=>{
            const eTarget = e.target;
            if(eTarget.classList.contains("modal-overlay")){
                modal.style.display = "none";
                screen.style.display="none";
                close.style.display="none";
            }
       });
    }

    function modalDataSetting(info = null){
        let frdt =document.getElementById("frdt");
        let todt =document.getElementById("todt");
        let frtm =document.getElementById("frtm");
        let totm =document.getElementById("totm");
        if(info!= null){
            frdt.value = info.dateStr;
            todt.value = info.dateStr;
            frtm.value = "08:00";
            totm.value = "18:00";
            
        }
        else{
            var yyyymmdd = new Date().toISOString().slice(0,10)
            var sYear = yyyymmdd.substring(0,4);
            var sMonth = yyyymmdd.substring(5,7);
            var sDate = yyyymmdd.substring(8,10);
            frdt.value = sYear+"-"+sMonth+"-"+sDate;
            todt.value = sYear+"-"+sMonth+"-"+sDate;
            frtm.value = "08:00";
            totm.value = "18:00";
        }
        
 
        let chkFrtm = document.querySelector("#frtm_C");
        let chkTotm = document.querySelector("#totm_C");
 
        chkFrtm.addEventListener("click", function(){
            if(chkFrtm.checked){
                frtm.disabled = true;
            }
            else{
                frtm.disabled = false;
            }
        });
 
        chkTotm.addEventListener("click", function(){
            if(chkTotm.checked){
                totm.disabled = true;
            }
            else{
                totm.disabled = false;
            }
        });
    }