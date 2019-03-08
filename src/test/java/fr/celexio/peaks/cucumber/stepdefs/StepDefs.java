package fr.celexio.peaks.cucumber.stepdefs;

import fr.celexio.peaks.PeaksApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = PeaksApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
