package com.projectboard.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("비즈니스 로직 - 페이지네이션")
//SpringBootTest 의 무게를 많이 줄여줄 수 있음. classes 는 설정 클래스 지정, 기본값이 root 임. 원래는 통합 테스트를 위해 root 하위의 모든 Bean Scan 대상들을 찾아줘야 하는데
//이 test 는 PaginationService test 만을 위한 것이므로 하나만 지정해서 빈 등록 가능 그게 classes
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,classes = PaginationService.class)
class PaginationServiceTest {
    //사실 PaginationService 는 인터페이스와 구현체가 분리되어 있지 않기 때문에 그냥 new 로 해서 test 해도 된다.
    private final PaginationService sut = new PaginationService();

//    public PaginationServiceTest(@Autowired PaginationService sut){
//        this.sut = sut;
//    }

    @DisplayName("현재 페이지 번호와 총 페이지 수를 주면, 페이징 바 리스트를 만들어준다.")
    @MethodSource
    @ParameterizedTest
    void givenCurrentNumberAndTotalPages_whenCalculating_thenReturnPaginationBarNumbers(int currentNumber, int totalPages, List<Integer> expected){

        //when
        List<Integer> actual = sut.getPaginationBarNumbers(currentNumber,totalPages);

        //then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> givenCurrentNumberAndTotalPages_whenCalculating_thenReturnPaginationBarNumbers() {
        return Stream.of(
                arguments(0,5,List.of(0,1,2,3,4)),
                arguments(0,13,List.of(0,1,2,3,4)),
                arguments(1,13,List.of(0,1,2,3,4)),
                arguments(2,13,List.of(0,1,2,3,4)),
                arguments(3,13,List.of(1,2,3,4,5)),
                arguments(4,13,List.of(2,3,4,5,6)),
                arguments(5,13,List.of(3,4,5,6,7)),
                arguments(10,13,List.of(8,9,10,11,12)),
                arguments(11,13,List.of(9,10,11,12)),
                arguments(12,13,List.of(10,11,12))
        );
    }

}