package inlineLinkUpdates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InlineLinksService {
    @Autowired
    private InlineLinksRepository inlineLinksRepository;
}
