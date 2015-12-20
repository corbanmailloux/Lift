// Check for localStorage
function storageAvailable(type) {
  try {
    var storage = window[type],
      x = '__storage_test__';
    storage.setItem(x, x);
    storage.removeItem(x);
    return true;
  }
  catch(e) {
    return false;
  }
}

// Test first
if (!storageAvailable("localStorage")) {
  document.getElementById("noLocalStorage").style.display = "block";
  document.getElementById("mainContent").style.display = "none";
}

var lastLift = 0;
var currentStreak = 1;
var bestStreak = 1;
var now = moment();

// Fragile: If the user removes either of the streak values, bad things happen.
if (localStorage.lastLift) {
  lastLift = moment.unix(localStorage.lastLift);
  currentStreak = localStorage.currentStreak;
  bestStreak = localStorage.bestStreak;
}

if (lastLift) {
  var daysBetween = (now.startOf("day")).diff((lastLift.startOf("day")), "days");

  switch (daysBetween) {
    case 0:
      // Already lifted today
      document.getElementById("headerText").innerHTML = "You already lifted today!";
      break;
    case 1:
      currentStreak++; // Intentional fallthrough
    default:
      if (daysBetween > 1) {
        // Too long; reset the streak
        currentStreak = 1;
      }

      document.getElementById("headerText").innerHTML = "Great lift. Same time tomorrow?";
      break;
  }
} else {
  document.getElementById("headerText").innerHTML = "Great first day. Same time tomorrow?";
}

// Update bestStreak
bestStreak = Math.max(bestStreak, currentStreak);

// Update lastLift
lastLift = now;

document.getElementById("currentStreak").innerHTML = currentStreak;
document.getElementById("bestStreak").innerHTML = bestStreak;

// Store the updated values
localStorage.setItem("lastLift", lastLift.unix());
localStorage.setItem("currentStreak", currentStreak);
localStorage.setItem("bestStreak", bestStreak);


/*
  Set up a countdown timer to indicate when the next lift can happen.
  Modified from: http://www.sitepoint.com/build-javascript-countdown-timer-no-dependencies/
*/
function getTimeRemaining(endtime) {
  var t = Date.parse(endtime) - Date.now();
  var seconds = Math.floor((t / 1000) % 60);
  var minutes = Math.floor((t / 1000 / 60) % 60);
  var hours = Math.floor((t / (1000 * 60 * 60)) % 24);
  return {
    'total': t,
    'hours': hours,
    'minutes': minutes,
    'seconds': seconds
  };
}

function initializeClock(id, endtime) {
  var clock = document.getElementById(id);

  function updateClock() {
    var t = getTimeRemaining(endtime);
    clock.innerHTML = (
      ('0' + t.hours).slice(-2) + ":" +
      ('0' + t.minutes).slice(-2) + ":" +
      ('0' + t.seconds).slice(-2)
    );

    if (t.total <= 0) {
      clearInterval(timeinterval);
    }
  }

  updateClock();
  var timeinterval = setInterval(updateClock, 1000);
}

initializeClock("countdown", now.startOf("day").add(1, "day"));
