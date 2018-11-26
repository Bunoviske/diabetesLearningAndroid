package com.example.bruno.diabeteslearning.Database;

import com.example.bruno.diabeteslearning.Carbohydrate.CarboDetector;

import java.util.List;

public interface LogListener {

    void onLogChanged(List<CarboDetector> log);
}
