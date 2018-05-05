var config = {
    apiKey: "AIzaSyCkpbfsJf7k8Ax1XDOD8C2pOKoh_7nul7c",
    authDomain: "stock-price-29e2b.firebaseapp.com",
    projectId: "stock-price-29e2b"
  };

firebase.initializeApp(config);

const firestore = firebase.firestore();
const settings = { timestampsInSnapshots: true };

firestore.settings(settings);

firestore.collection("stocks").get().then((snapshot) => {
    snapshot.forEach((doc) => {
	$("#stock-row").append(docToHtml(doc));
    });
});


function docToHtml(doc) {
    var html = '';
    const price = doc.data().price.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
    const volume = doc.data().volume.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
    const date = new Date(doc.data().timestamp.seconds * 1000);

    html += '<div class="col-md-4">';
    html += '<div class="card mb-4 box-shadow">';
    html += '<div class="card-header">';
    html += '<h4 class="text-center">' + doc.id + '</h4>';
    html += '</div>';
    html += '<div class="card-body">';
    html += '<p class="card-text text-center">Price: $' + price + '</p>';
    html += '<p class="card-text text-center">Volume:  ' + volume + ' units</p>';
    html += '<div class="d-flex justify-content-between align-items-center">';
    html += '<div class="btn-group">';
    html += '<button type="button" class="btn btn-sm btn-outline-secondary">View</button>';
    html += '<button type="button" class="btn btn-sm btn-outline-secondary">Edit</button>';
    html += '</div>';
    html += '<small class="text-muted">' + date.toLocaleString("en-US") + '</small>';
    html += '</div>';
    html += '</div>';
    html += '</div>';
    html += '</div>';
    return html;
}
