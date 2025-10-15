package com.example.jwt_token.controller;

import com.example.jwt_token.dto.AnswerRequest;
import com.example.jwt_token.model.Answer;
import com.example.jwt_token.service.AnswerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
@AllArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @GetMapping
    public ResponseEntity<List<Answer>> getAnswers(@PathVariable Long questionId) {
        return ResponseEntity.ok(answerService.getAnswersByQuestionId(questionId));
    }

    @PostMapping
    public ResponseEntity<Answer> createAnswer(
            @PathVariable Long questionId,
            @RequestBody AnswerRequest answerRequest
    ) {
        answerRequest.setQuestionId(questionId);
        return ResponseEntity.ok(answerService.createAnswer(questionId, answerRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Answer> updateAnswer(
            @PathVariable Long questionId,
            @PathVariable Long id,
            @RequestBody AnswerRequest request
    ) {
        request.setQuestionId(questionId); // set otomatis
        return ResponseEntity.ok(answerService.updateAnswer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAnswer(
            @PathVariable Long questionId,
            @PathVariable Long id
    ) {
        answerService.deleteAnswer(id, questionId);
        return ResponseEntity.ok("Answer deleted successfully.");
    }
}
