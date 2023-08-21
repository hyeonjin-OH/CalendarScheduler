package hyeonjin.calendar.web.sample;

import hyeonjin.calendar.domain.sample.Sample;
import hyeonjin.calendar.domain.sample.SampleRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class SampleController {
    private final SampleRepository sampleRepository;

    public SampleController(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    public String register(@Valid @ModelAttribute("sample") Sample sample, BindingResult bindingResult,
                            HttpServletRequest request) {

        sampleRepository.save(sample);
        sampleRepository.findByComment("test");

        return "/test";
    }
}