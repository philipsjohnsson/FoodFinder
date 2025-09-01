package se.umu.cs.phjo0015.mapapplication.database

import org.json.JSONObject

fun getDataset(): List<Destination> {
    val dataset: List<Destination> = listOf(
        Destination(57.710984, 12.006829, "Lejonet & Björnen", "Här finns god glass att äta."),
        Destination(57.710988, 11.987131, "Benne pastabar", "God pasta för en schysst peng")
    )

    return dataset
}