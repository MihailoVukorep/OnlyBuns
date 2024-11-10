function setup() {
	var cnv = createCanvas(400, 400);
	cnv.id('aclock');
	cnv.style('display', 'block');
	cnv.parent('clock_box');
	
	angleMode(DEGREES);
}

function draw() {
	clear();
	translate(200, 200);

	
	fill(255);
	noStroke(2);
	let fontSize = 50;
	textSize(fontSize);

	rotate(-90);
	
	let hr = hour();
	let mn = minute();
	let sc = second();
	
	let dis = 360;
	let disf = 40;

	// all
	strokeWeight(15);
	noFill();

	// sec
	stroke(255, 100, 150);
	let secondAngle = map(sc, 0, 60, 0, 360);
	arc(0, 0, dis, dis, 0, secondAngle);
	
	// min
	stroke(150, 100, 255);
	let minuteAngle = map(mn, 0, 60, 0, 360);
	arc(0, 0, dis-disf, dis-disf, 0, minuteAngle);
	
	// hour
	stroke(150, 255, 100);
	let hourAngle = map(hr % 12, 0, 12, 0, 360);
	arc(0, 0, dis-disf*2, dis-disf*2, 0, hourAngle);
	
	push();
	strokeWeight(3);
	rotate(secondAngle);
	stroke(255, 100, 150);
	line(0, 0, 125, 0);
	pop();
	
	push();
	strokeWeight(6);
	rotate(minuteAngle);
	stroke(150, 100, 255);
	line(0, 0, 100, 0);
	pop();
	
	push();
	strokeWeight(9);
	rotate(hourAngle);
	stroke(150, 255, 100);
	line(0, 0, 75, 0);
	pop();
	
	stroke(232, 193, 69);
	strokeWeight(20);
	point(0, 0);
}
