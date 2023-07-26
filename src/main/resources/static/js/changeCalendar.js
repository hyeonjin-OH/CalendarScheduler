$("#delete-button").click(function(e){
    var str = ""
    var tdArr = new Array();    // 배열 선언
    var checkBtn = $(this);

    var tr = checkBtn.parent().parent();
    var td = tr.children();

    var id = td.eq(0).text().trim();
    var seqn = td.eq(2).text().trim();

        e.preventDefault();
        $.ajax({
            url : "/Calendar/delete"
            ,data : {"ctgrId" : id, "ctgrSeqn" : seqn}
            ,type: "post"
            ,success : function(data){
                console.log("data : "+data);
                location.reload();
            },error : function(req,status,err){
               console.log(req);
            }
        });

});


$(".change-button ").click(function(e){
    var str = ""
    var tdArr = new Array();    // 배열 선언
    var checkBtn = $(this);

    var tr = checkBtn.parent().parent();
    var td = tr.children();

    var id = td.eq(0).text().trim();
    var seqn = td.eq(2).text().trim();

    e.preventDefault();
    $.ajax({
        url : "/Calendar/change"
        ,data : {"mbrId" : id, "mbrSeqn" : seqn}
        ,type: "post"
        ,success : function(data){
            console.log("data : "+data);
            location.reload();
        },error : function(req,status,err){
                        console.log(req);
                     }
    });

});