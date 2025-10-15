package com.example.jwt_token.service;

import com.example.jwt_token.dto.AnswerRequest;
import com.example.jwt_token.model.Answer;
import com.example.jwt_token.model.Question;
import com.example.jwt_token.repository.AnswerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionService questionService;

    // Get Answers by Question ID
    public List<Answer> getAnswersByQuestionId(Long questionId) {
        Question question = questionService.getQuestionById(questionId);
        if (question == null) {
            throw new IllegalArgumentException("Question with ID " + questionId + " not found.");
        }
        return question.getAnswers();
    }

    // Create Answers for a Question
    public Answer createAnswer(Long questionId, AnswerRequest answerRequest) {
        Question question = questionService.getQuestionById(questionId);
        if (question == null) {
            throw new IllegalArgumentException("Question not found");
        }

        Answer answer = new Answer();
        answer.setTeks(answerRequest.getTeks());
        answer.setBobot(answerRequest.getBobot());
        answer.setIsanswer(answerRequest.getIsanswer());
        answer.setQuestion(question);
        answer.setCreatedAt(System.currentTimeMillis());
        answer.setUpdatedAt(System.currentTimeMillis());
        return answerRepository.save(answer);
    }

    // Update Answer
    public Answer updateAnswer(Long id, AnswerRequest answerRequest) {
        Answer existingAnswer = answerRepository.findById(id).orElse(null);

        Question question = questionService.getQuestionById(answerRequest.getQuestionId());
        if (question == null) {
            throw new IllegalArgumentException("Question not found");
        }

        existingAnswer.setTeks(answerRequest.getTeks());
        existingAnswer.setBobot(answerRequest.getBobot());
        existingAnswer.setIsanswer(answerRequest.getIsanswer());
        existingAnswer.setQuestion(question);
        existingAnswer.setUpdatedAt(System.currentTimeMillis());
        return answerRepository.save(existingAnswer);
    }

    // Delete Answer By ID & Question ID
    public boolean deleteAnswer(Long id, Long questionId) {
        Answer existing = answerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Answer with ID " + id + " not found."));

        if (!existing.getQuestion().getId().equals(questionId)) {
            throw new IllegalArgumentException("Answer does not belong to Question ID " + questionId);
        }

        answerRepository.delete(existing);
        return true;
    }
}
