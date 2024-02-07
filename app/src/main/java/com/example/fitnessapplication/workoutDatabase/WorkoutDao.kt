package com.example.fitnessapplication.workoutDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout")
    fun getAllWorkouts(): List<Workout>

    @Query("SELECT * FROM exercise WHERE workoutId = :workoutId")
    fun getExercisesForWorkout(workoutId: Int): List<Exercise>

    @Query("SELECT * FROM `set` WHERE exerciseId = :exerciseId")
    fun getSetsForExercise(exerciseId: Int): List<Set>

    @Insert
    suspend fun insertWorkout(workout: Workout): Long

    @Delete
    suspend fun deleteWorkout(workout: Workout)

    @Insert
    suspend fun insertExercise(exercise: Exercise): Long

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Insert
    suspend fun insertSet(set: Set): Long

    @Delete
    suspend fun deleteSet(set: Set)
}