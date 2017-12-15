"use strict"; // Strict moda alır. Kullanıcı tanımlamalarını olası hatalara karşı denetler.
console.log('Server çalıştırıldı.'); //Konsolda parametre olarak verilen değeri yazdırır.
var express = require("express"); // Express kütüphanesini projeye dahil eder ancak bu küpüthaneyi kullanmak için konsolda 
// npm install express kodu yazılmalıdır.

var bodyParser = require("body-parser"); // Kütüphanesini ekler ancak bunun içinde aşağıda ki kodu consolda yazmak gerekiyor.
// npm install body-parser

var app = express(); // Bir nesne türeterek express kütüphanesinin tüm metotlarına ulaşabiliyoruz.

// Bu tanımlamalar gelen isteğin yada gönderilen cevabın body kısmına ulaşabilmek içindir.
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

var fs = require("fs"); // Dosya işlemleri içinde bu kütüphaneyi tanımlamamız gerekiyor.

fs.exists("db", function (val) { // Bu metot kök dizinde 'db' adında bir dosya olup olmadığını kontrol eder. 
	//Sonucu boolean tipinde val değişkenine aktarır.
 
    if (val) { // Eğer böyle bir varsa bu blok çalışır.
        console.log("Veritabanı dizini mevcut.");
		fs.exists("db/database.json", function(val){ // Bu dosyanın içinde de 'database.json' dosyasının olup olmadığına bakar.
			if(val){ // Böyle bir dosya varsa bu satır çalışır.
				console.log("Veritabanı dosyası mevcut.");
			}else{ // Yoksa oluşturulmak üzere bu blok çalışır.
				fs.writeFile("db/database.json",null, function (ex){ // db klasörünün içinde database.json dosyası oluşrularak içerisine
				// null değeri yazılır.
					if (ex) { // Hata oluşursa bu blok çalışır.
						console.log("Veritabanı dosyası oluşturulamadı.");
					}
					else{ // Hata yoksa bu blok çalışır.
						console.log("Veritabanı dosyası da oluşturuldu.");
					}
				});
			}
		});
    } else { // Eğer kök dizinde db adında klasör yoksa bu blok çalışır.
		fs.mkdir("db", function (ex) 
				{ // db adında bir klasör oluşturulur.
					if (ex) { // Hata oluşursa bu satır çalışır.
						console.log("Veritabanı dizini oluşturulamadi.");
					}else{ // Hata oluşmazsa bu satır çalışır.
						console.log("Veritabanı dizini oluşturuldu.");
						fs.writeFile("db/database.json",null, function (ex) 
						{ // db klasörünün içerisinde database.json dosyası oluşturularak içerisine null değeri eklenir.
							if (ex) {
								console.log("Veritabanı dosyası oluşturulamadı.");
							}
							else{
								console.log("Veritabanı dosyası da oluşturuldu.");
							}
						});
					}
				});
    }
});
app.post("/kisi-ekle",function(req,res)
{	// Post metodu kullanılarak kisi ekleme işlemi bu blok ile gerçekleşir.
	console.log("kisi ekle")
	console.log(req.body); // Sunucuya gönderilen isteğin parametrelerini gösterir.
	var c = req.body; // İsteğimizin parametreleri c değişkeninde saklanır.
	
	// kişi bilgilerinin istediğimizle gelip gelmediğini kontrol ediyoruz. Tüm kişi bilgileri gelmişse program akışı devam eder.
	// Aksi halde aşağıda ki if blokları ile 400 numaralı sunucusu hatası döndürür.
	if(c.id == undefined){
		res.sendStatus(400);
		return;
	}
	else if(c.isim == undefined){
		res.sendStatus(400);
		return;
	}
	else if(c.soyisim == undefined){
		res.sendStatus(400);
		return
	}
	else if(c.numara == undefined){
		res.sendStatus(400);
		return
	}
	else if(c.mail == undefined){
		res.sendStatus(400);
		return
	}
	else if(c.cinsiyet == undefined){
		res.sendStatus(400);
		return
	}
	
	fs.readFile("db/database.json", "utf8", function (ex, data) 
	{	// Dosya okuma işlemi yapar	
		if (ex) 
		{ 	// Dosya okunamadıysa hata döndürür
			console.log(ex);
		} else 
		{		//Dosya okuma işleminde hata yoksa bu blok çalışır.
				var kisiler = []; // Dosyada daha önce saklanan kişiler bu diziye aktarılacaktır. Bu yüzden bu dizi tanımlanmıştır.
				var d; // Okunan veriyi saklamak için tanımlandı.
				d = JSON.parse(data); // Okunan veri önce data değişkeninde saklanır sonrada bu satır ile d değişkenine aktarılacaktır
				if(d!=null) 
				{	// Eğer dosya da herhangi bir kayıt verisi varsa yani null değerinden farklıysa bu blok çalışır.
					kisiler = d; // Dosyada ki tüm kişileri diziye aktarır.
					
					/* Mobil uygulama üzerinden her kaydın id değerine sıfır değerini veriyorduk. 
					  Bu veri sunucuya gelmeden önce ise listede ki en büyük veri tespit edildikten sonra onun bir fazlası 
					  ile değiştirilir. Yani özetle dosyada hiç veri olsun yada olmasın mobil uygulama üzerinde kişinin id 
					  değerine sıfır değeri verilir. Sonra uygulama üzerinden tüm kayıtların id numarasını gezerek en büyük 
					  id değeri bulunur ve bunun bir fazlası ile değiştirilir. Bu sayede veritabanında id otamatik artışına
					  benzer bir yapı kurulmuş olur. Aşağıda ki kodlar da bu yapıyı sağlar. */
					var id = 0
					var max =0
					for(var i=0;i<kisiler.length;i++){
						id = kisiler[i].id
						if(id>max){
							max = id
						}
					}
					max++;
					c.id = max
				}
				kisiler.push(c); // Diziye mobil uygulamadan gelen kişi verileri eklenir.
				d = JSON.stringify(kisiler); // Dizi string tipine çevrilir. Daha sonra d değişkenine aktarılır.
				fs.writeFile("db/database.json",d, function (ex) 
				{	// Dosyaya kişi listemizin son hali yazılır.
					if (ex) 
					{
						console.log("Kişi eklenemedi!");
						res.end("Kisi eklenemedi!");
					}else
					{
						res.end("Kisi eklendi.");
						console.log("Kisi eklendi.");
					}
				});				
		}
	});
});

app.post("/kisi-guncelle",function(req,res)
{
	var c = req.body; // Sunucuya gönderilen istediğin parametreleri c değişkenine aktarılır.
	
	// Parametrelerin içinde tüm kullanıcı verilerinin varlığı kontrol edilir. Yoksa 400 numaralı sunucu hatası döndürür.
	// Tüm veriler mevcutsa yoksa program akışı devam eder.
	if(c.id == undefined){
		res.sendStatus(400);
		return;
	}
	else if(c.isim == undefined){
		res.sendStatus(400);
		return;
	}
	else if(c.soyisim == undefined){
		res.sendStatus(400);
		return
	}
	else if(c.numara == undefined){
		res.sendStatus(400);
		return
	}
	else if(c.mail == undefined){
		res.sendStatus(400);
		return
	}
	else if(c.cinsiyet == undefined){
		res.sendStatus(400);
		return
	}
	
	fs.readFile("db/database.json", "utf8", function (ex, data) 
	{	// Dosya okunur ve sonuc data değişkeninde saklanır.	
		if (ex) 
		{
			console.log(ex); // Hata olduğunda consolda hata mesajı bastırır.
		} else 
		{
				var kisiler = [];
				var d;
				d = JSON.parse(data); // Dosyada ki tüm veriler d değişkeninde saklanır
				kisiler = d; // Tüm kişiler diziye aktarılır.
				for(var i = 0;i<kisiler.length;i++){ // Bu blokta tüm kişi id değerleri telefondan gelen id değeri
				// ile karşılaştırılarak detaylarında güncelleme yapmak istediğimiz kişiye ulaşmaya çalışıyoruz.
				
					var k = kisiler[i].id; // Tüm kişilerin id değeri
					var l = c.id; // Telefondan gelen id
					if(k == l){ // Eşleşme olduğunda o kişinin verikeri telefondan gelen güncel veri ile değiştirilir.
						kisiler[i].id = c.id
						kisiler[i].isim = c.isim
						kisiler[i].soyisim = c.soyisim
						kisiler[i].numara = c.numara
						kisiler[i].mail = c.mail
						kisiler[i].cinsiyet = c.cinsiyet
					}
				}
			
				d = JSON.stringify(kisiler); // Dizimiz string tipine çevrilerek d değişkeninde saklanır.
				fs.writeFile("db/database.json",d, function (ex) 
				{	// Güncel olan kişi verisi d değişkeni ile dosyaya yazılır.
					if (ex) 
					{
						console.log("Güncelleme işlemi başarısız!");
						res.end("Guncelleme islemi basarisiz!");
					}else
					{
						res.end("Kisi guncellendi.");
						console.log("Kişi güncellendi.");
					}
				});				
		}
	});
});

app.get("/kisi-sil",function(req,res)
{	// Kişi silme işlemini yapacağımız metot
	var c = req.query; // İsteğimizin tüm parametreleri c değişkenine yüklenir.
	
	if(c.id == undefined){ // Silinecek kişinin id değerinin var olup olmadığını kontrol eder.
		res.sendStatus(400);
		return;
	}
	
	fs.readFile("db/database.json", "utf8", function (ex, data) 
	{	// Dosyayı okuma işlemi yapılır. Okunan veriler data değişkeninde saklanır.	
	
		if (ex){	// Hata oluşursa bu blok çalışır.
		
			console.log(ex); // Hata mesajı döndürür.
			
		} else {	// Hata yoksa bu blok çalışır.
		
			var kisiler = [];
			var d;
			d = JSON.parse(data);
			kisiler = d;
			for(var i = 0;i<kisiler.length;i++){ // Telefondan gelen id değeri ile tüm kişilerin id değeri eşit mi diye
			// kontrol edilir. Eşleşen kişi diziden silinir.
				var k = kisiler[i].id;
				var l = c.id; // Telefondan gelen id
				if(k == l){	// Tüm kişi verileri ile telefondan gelen id karşılaştırılır.
					kisiler.splice(i, 1);  // Silme işlemi bu satırda yapılır.
				}
			}
			d = JSON.stringify(kisiler); // Güncel kişi dizisi string tipinde d değişkenine aktarılır.
			fs.writeFile("db/database.json",d, function (ex) 
			{	// Güncel kişi listesi dosyaya yazdırılır.
				if (ex) 
				{	// Hata varsa bu blok çalışır
					console.log("Silme işlemi başarısız.");
					res.end("Silme islemi basarisiz.");
				}else
				{	// Hata yoksa bu blok çalıştırılır.
					res.end("Silme islemi basarili."); 	// Sunucuya gönderilen veri
					console.log("Silme işlemi başarılı.");
				}
			});				
		}
	});
});

app.get("/kisi-ara",function(req,res)
{	// Kişi arama işlemlerini gerçekleştiren metot
	var c = req.query;
	console.log(c)
	if(c.isim == undefined){ // Aranan kişinin isminin tanımlı olması gerekir. Aksi halde hata mesajı döndürür.
		res.sendStatus(400);
		return;
	}
	
	fs.readFile("db/database.json", "utf8", function (ex, data) 
	{		
		if (ex) 
		{
			console.log(ex);
		} else 
		{
				var kisiler = [];
				var d;
				d = JSON.parse(data);
				if(d!=null) 
				{	// Dosyada kişi ekli olduğundan emin olunur.
					var z = [];
					kisiler = d;
					for(var i = 0;i<kisiler.length;i++){
						var k = kisiler[i].isim; // Tüm kişi isimleri bu değişkene aktarılır.
						var l = k.search(c.isim); // Telefondan gelen isim tüm kişi isimleri ile karşılaştırılır.

						if(l == 0){ // Sonuç sıfır ise eşleşme var demektir.
							z.push(kisiler[i]); // Eşleşen tüm kişiler listeye eklenir.
							console.log(z)
						}
					}
					if(z.length == 0) {	// Dizinin boyutu sıfır ise bu aradığımız kişinin listesinde olmadığını gösterir.
						res.send("Eslesme yok");
					} else { // Ancak dizi boyutu sıfırdan büyükse o halde dizide elaman var demektir.
						res.json(z); // Dizi Json formatına çevrilerek cevap olarak gönderilir.
					}
					
				}						
		}
	});
});

app.get("/kisi-yukle",function(req,res)
{	// Bu metot ile tüm kişileri listelemeyi sağlarız
	fs.readFile("db/database.json", "utf8", function (ex, data) 
	{	// Dosya okunarak tüm kişiler data değişkeninde saklanır.	
		if (ex) 
		{	// Hata olursa bu blok çalışır.
			console.log(ex);
		} else 
		{		// Hata yoksa bu blok çalışır.
	
				var kisiler = [];
				var d;
				d = JSON.parse(data); // Dosyada ki veriler Json farmatına çevrilerek diziye aktarılır.
				kisiler = d;
				console.log(kisiler);
				res.send(kisiler); // Kisiler dizim cevap olarak mobil uygulamaya gönderilir.
		}
	});
});

app.listen(8080);	// Uygulamam sürekli bu portu dinleyerek gelen isteklere cevap verir.