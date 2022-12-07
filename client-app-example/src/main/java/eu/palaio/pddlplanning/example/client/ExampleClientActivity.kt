package eu.palaio.pddlplanning.example.client

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import eu.palaio.pddlplanning.IPDDLPlannerService
import eu.palaio.pddlplanning.PermissionCheckFunction
import eu.palaio.pddlplanning.createPlanSearchFunctionFromService
import eu.palaio.pddlplanning.createPlannerPermissionChecker
import eu.palaio.pddlplanning.utils.createAsyncCoroutineScope
import kotlinx.coroutines.launch

private const val plannerServicePackage = "eu.palaio.pddlplanning.example.service"

class ExampleClientActivity : AppCompatActivity() {

    private val coroutineScope = createAsyncCoroutineScope()
    private lateinit var permissionCheckFunction: PermissionCheckFunction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example_main)
        permissionCheckFunction = createPlannerPermissionChecker(this, plannerServicePackage)
    }

    override fun onResume() {
        super.onResume()
        val mainText = findViewById<TextView>(R.id.mainText)
        mainText.text = "Searching plan..."
        val checkingPermission = permissionCheckFunction()
        coroutineScope.launch {
            try {
                checkingPermission.await()
                val plannerServiceIntent = Intent(IPDDLPlannerService.ACTION_SEARCH_PLANS_FROM_PDDL)
                plannerServiceIntent.`package` = plannerServicePackage
                val planSearchFunction =
                    createPlanSearchFunctionFromService(
                        this@ExampleClientActivity,
                        plannerServiceIntent
                    )
                val domain = "(define (domain hello_domain)\n" +
                        "   (:requirements :adl :negative-preconditions :universal-preconditions)\n" +
                        "   (:types)\n" +
                        "   (:constants)\n"
                "   (:predicates\n" +
                        "       (was_greeted ?o)\n" +
                        "   )\n" +
                        "   (:action hello\n" +
                        "       :parameters (?o)\n" +
                        "       :precondition ()\n" +
                        "       :effect (was_greeted ?o)\n" +
                        "   )\n" +
                        ")"

                val problem = "(define (problem hello_problem)\n" +
                        "   (:domain hello_domain)\n" +
                        "   (:requirements :adl :negative-preconditions :universal-preconditions)\n" +
                        "   (:objects\n" +
                        "       world\n" +
                        "   )\n" +
                        "   (:init)\n"
                "   (:goal\n" +
                        "       (forall (?o) (was_greeted ?o))\n" +
                        "   )\n" +
                        ")"

                val plan = planSearchFunction(domain, problem, null)

                runOnUiThread {
                    mainText.text = "Found plan:\n${plan.joinToString("\n")}"
                }
            } catch (e: Throwable) {
                runOnUiThread {
                    mainText.text = "Error: $e\nSee logs for more details."
                    e.printStackTrace()
                }
            }
        }
    }
}