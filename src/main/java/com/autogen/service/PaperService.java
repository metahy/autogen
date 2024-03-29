package com.autogen.service;

import com.autogen.domain.Paper;

import java.util.List;

public interface PaperService {
    List<Paper> getAllPapers();

    int delete(String id);

    Paper addPaper(Paper paper);

    Paper getPaperById(String id);

    Paper updatePaper(Paper paper);
}
