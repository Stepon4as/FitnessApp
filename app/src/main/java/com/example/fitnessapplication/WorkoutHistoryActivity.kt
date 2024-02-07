package com.example.fitnessapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapplication.workoutDatabase.Exercise
import com.example.fitnessapplication.workoutDatabase.Workout
import com.example.fitnessapplication.workoutDatabase.WorkoutDao
import com.example.fitnessapplication.workoutDatabase.WorkoutDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

class WorkoutHistoryActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db by lazy {
            WorkoutDatabase.getDatabase(applicationContext)
        }
        val workoutDao = db.workoutDao()

        GlobalScope.launch(Dispatchers.IO) {
            val workouts = workoutDao.getAllWorkouts()
            withContext(Dispatchers.Main) {
                setContent {
                    MaterialTheme {
                        Surface(color = MaterialTheme.colors.background) {
                            RecyclerView(workouts, workoutDao)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListItem(workout: Workout, workoutDao: WorkoutDao) {

    val expanded = remember { mutableStateOf(false) }
    val extraPadding by animateDpAsState(
        if (expanded.value) 24.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )
    val coroutineScope = rememberCoroutineScope()
    val exercises = remember { mutableStateOf(listOf<Exercise>()) }

    Surface(
        color = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Row {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text = "Workout: ${workout.id}")
                    val format = SimpleDateFormat("yyyy-MM-dd")
                    Text(text = "Date: ${format.format(Date(workout.startTime))}")
                    Text(text = "End Time: ${workout.endTime}")
                }
                OutlinedButton(onClick = { expanded.value = !expanded.value }) {
                    Text(if (expanded.value) "Show less" else "Show more")
                }
            }
            if (expanded.value) {
                coroutineScope.launch(Dispatchers.IO) {
                    val fetchedExercises = workoutDao.getExercisesForWorkout(workout.id)
                    withContext(Dispatchers.Main) {
                        exercises.value = fetchedExercises
                    }
                }
                Column(
                    modifier = Modifier.padding(
                        bottom = extraPadding.coerceAtLeast(0.dp)
                    )
                ) {
                    Text(text = "")
                    exercises.value.forEach { exercise ->
                        Text(text = "")
                        Text(text = "${exercise.title}:")
                        val sets = remember { mutableStateOf(listOf<com.example.fitnessapplication.workoutDatabase.Set>()) }
                        coroutineScope.launch(Dispatchers.IO) {
                            val fetchedSets = workoutDao.getSetsForExercise(exercise.id)
                            withContext(Dispatchers.Main) {
                                sets.value = fetchedSets
                            }
                        }
                        sets.value.forEach { set ->
                            Text(text = "${set.reps} x ${set.weight}kg")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListExercises(exercises: List<Exercise>) {
    LazyColumn(modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
        items(items = exercises) { exercise ->
            Column {
                Text(text = "Exercise: ${exercise.title}")
            }
        }
    }
}

@Composable
fun RecyclerView(workouts: List<Workout>, workoutDao: WorkoutDao) {
    LazyColumn(modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
        items(items = workouts) { workout ->
            ListItem(workout, workoutDao)
        }
    }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        Surface(color = MaterialTheme.colors.background) {
            //RecyclerView()
        }
    }
}


