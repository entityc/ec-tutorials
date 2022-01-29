package org.entitycompiler.tutorial.web.user;

import org.entitycompiler.tutorial.dto.ExerciseDto;
import org.entitycompiler.tutorial.dto.ModuleDto;
import org.entitycompiler.tutorial.dto.SessionDto;
import org.entitycompiler.tutorial.dto.StepDto;
import org.entitycompiler.tutorial.dto.TutorialDto;
import org.entitycompiler.tutorial.exception.ServiceException;
import org.entitycompiler.tutorial.model.Module;
import org.entitycompiler.tutorial.model.Session;
import org.entitycompiler.tutorial.model.User;
import org.entitycompiler.tutorial.security.PersistentUserDetailsService;
import org.entitycompiler.tutorial.security.SecurityService;
import org.entitycompiler.tutorial.service.ExerciseService;
import org.entitycompiler.tutorial.service.ModuleService;
import org.entitycompiler.tutorial.service.SessionService;
import org.entitycompiler.tutorial.service.StepService;
import org.entitycompiler.tutorial.service.TutorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

@Controller
public class UserWebPageController {
    @Autowired
    private SecurityService securityService;

    @Autowired
    private PersistentUserDetailsService userDetailsService;

    @Autowired
    private TutorialService tutorialService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private ExerciseService exerciseService;
    @Autowired
    private StepService stepService;

    @GetMapping(value = {"/"})
    public String homePage(Model model) throws ServiceException {
        model.addAttribute("TutorialList", tutorialService.getTutorialDtoList(0, 100, true));
        User user = userDetailsService.findByEmail(securityService.findLoggedInUsername());
        model.addAttribute("loggedInUser", user);
        return "UserHome";
    }

    @GetMapping(value = {"/tutorial/{id}"})
    public String tutorialPage(Model model, @PathVariable("id") UUID tutorialId) throws ServiceException {
        TutorialDto tutorial = tutorialService.getTutorialDtoById(tutorialId);
        model.addAttribute("Tutorial", tutorial);
        model.addAttribute("ModulesList", moduleService.getModuleDtoListByTutorial(tutorialId, 0, 100, true));
        User user = userDetailsService.findByEmail(securityService.findLoggedInUsername());
        model.addAttribute("loggedInUser", user);
        return "UserTutorial";
    }

    @GetMapping(value = {"/module/{id}"})
    public String modulePage(Model model, @PathVariable("id") UUID moduleId) throws ServiceException {
        ModuleDto moduleDto = moduleService.getModuleDtoById(moduleId);
        Module module = moduleService.getModuleById(moduleId);
        TutorialDto tutorial = tutorialService.getTutorialDtoById(module.getTutorialId());
        model.addAttribute("Tutorial", tutorial);
        model.addAttribute("Module", moduleDto);
        model.addAttribute("SessionsList", sessionService.getSessionDtoListByModule(moduleId, 0, 100, true));
        User user = userDetailsService.findByEmail(securityService.findLoggedInUsername());
        model.addAttribute("loggedInUser", user);
        return "UserModule";
    }

    @GetMapping(value = {"/session/{id}"})
    public String sessionPage(Model model, @PathVariable("id") UUID sessionId) throws ServiceException {
        SessionDto sessionDto = sessionService.getSessionDtoById(sessionId);
        Session session = sessionService.getSessionById(sessionId);
        ModuleDto moduleDto = moduleService.getModuleDtoById(session.getModuleId());
        Module module = moduleService.getModuleById(session.getModuleId());
        TutorialDto tutorial = tutorialService.getTutorialDtoById(module.getTutorialId());
        model.addAttribute("Tutorial", tutorial);
        model.addAttribute("Module", moduleDto);
        model.addAttribute("Session", sessionDto);
        Vector<SessionDto> moduleSessions = new Vector<>(sessionService.getSessionDtoListByModule(module.getId(), 0, 100, true));
        int indexInList = -1;
        for (int i=0;i<moduleSessions.size();i++) {
            if (moduleSessions.elementAt(i).getNumber() == sessionDto.getNumber()) {
                indexInList = i;
                break;
            }
        }
        SessionDto prevSessionDto = (indexInList > 0) ? moduleSessions.elementAt(indexInList-1) : null;
        SessionDto nextSessionDto = (indexInList < (moduleSessions.size()-1)) ? moduleSessions.elementAt(indexInList+1) : null;
        model.addAttribute("prevSession", prevSessionDto);
        model.addAttribute("nextSession", nextSessionDto);
        // set SessionMarkdown here with the markdown document for the session
        User user = userDetailsService.findByEmail(securityService.findLoggedInUsername());
        model.addAttribute("loggedInUser", user);
        return "UserSession";
    }
}
