package com.autogen.service.impl;

import com.autogen.dao.PaperDao;
import com.autogen.domain.Paper;
import com.autogen.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaperServiceImpl implements PaperService {

	@Autowired
	private PaperDao paperDao;
	
	public List<Paper> getAllPapers() {
		return paperDao.selectAllPapers();
	}

	@Override
	public int delete(String id) {
		return paperDao.deletePaperById(id);
	}

	@Override
	public Paper addPaper(Paper paper) {
		return paperDao.insertPaper(paper) > 0 ? paper : null;
	}

	@Override
	public Paper getPaperById(String id) {
		return paperDao.selectPaperById(id);
	}

	@Override
	public Paper updatePaper(Paper paper) {
		return paperDao.updatePaper(paper) > 0 ? paper : null;
	}

}
