init();

async function init() {
    load_analytics();
}

async function load_analytics() {
    const response = await fetch("/api/admin/analytics");
    const json = await response.json();
    drawChart([json.usersPostedPercentage, json.usersCommentedPercentage, json.noActivityUsersPercentage]);
}

function drawChart(values){
    let cumulative = 15;
    const radius = 150;
    const circumference = 2 * Math.PI * radius;

    let offset = 0;
    setSection("section1", values[0], cumulative, circumference);
    cumulative += (values[0] / 100) * circumference;

    setSection("section2", values[1], cumulative, circumference);
    cumulative += (values[1] / 100) * circumference;

    setSection("section3", values[2], cumulative, circumference);
}

function setSection(id, value, cumulativeOffset, circumference) {
    const circle = document.getElementById(id);
    let length = (value / 100) * circumference - 15;
    if(length < -15){
        circle.style.visibility = 'hidden';
    }
    else if(length < 0){
        length = 0;
    }
    circle.style.strokeDasharray = `${length} ${circumference}`;
    circle.style.strokeDashoffset = -cumulativeOffset;
}
document.addEventListener('DOMContentLoaded', function() {
    setGraphHoverShadow("section1", "legendItem1");
    setGraphHoverShadow("section2", "legendItem2");
    setGraphHoverShadow("section3", "legendItem3");
});
function setGraphHoverShadow(circleSectionId, legendItemId){
    const circleSection = document.getElementById(circleSectionId);
    const legendItem = document.getElementById(legendItemId);

    circleSection.addEventListener('mouseover', function() {
        legendItem.classList.add('hover-shadow');
    });

    circleSection.addEventListener('mouseout', function() {
        legendItem.classList.remove('hover-shadow');
    });
}
