package com.roxy.sudoku;



public class SudokuLogic {

	// 난이도에 따른 blind Cell 블록 수 설정
	private static int[] levelBlindCount = new int[] { 30, 50, 80 };
	public final int level1 = 0;
	public final int level2 = 1;
	public final int level3 = 2;

	// 해법을 위한 Queue 크기 설정
	private static int queSize = 1024;

	// 블록 크기 설정
	public final static int widthCell = 3, heightCell = 3, block = 3;

	public final int totalWidthCell = widthCell * block, totalHeightCell = heightCell * block,
			totalCell = widthCell * heightCell * block * block, blockCell = widthCell * heightCell;

	// 맵
	public int[] originalMap = new int[totalCell]; // 문제 풀이 답안
	public int[] blindMap = new int[totalCell]; // 사용자가 풀게 될 문제

	// 해법을 위한 Queue Data 클래스
	class MapQueData {
		int index;
		int number;

		int[] map = new int[totalCell];
	}

	// 클래스 생성자
	public SudokuLogic() {
		initMap();
	}

	// 맵 초기화
	public void initMap() {
		int i = 0;

		for (i = 0; i < totalCell; i++) {
			originalMap[i] = 0;
			blindMap[i] = 0;
		}
	}

	// 배열에 등록된 수를 하나씩 가져오기 위함.
	public int getBlindNumber(int index) {
		return blindMap[index];
	}

	// 결과 작성후 값 비교 (결과 보기 버튼 시 사용. 0이 틀린거, 1이 맞는거)
	public int getComparResult() {
		for (int i = 0; i < totalCell; i++) {
			if (originalMap[i] != blindMap[i])
				return 0;
		}

		return 1;
	}

	// 특정 인덱스의 정답 값 보기
	public int getOriginalNumber(int index) {
		return originalMap[index];
	}

	// 난이도 버튼에 넣을때 쓸 것. 0이 쉬움, 1이 보통, 2가 어려움.
	public void setDifficult(int diff) {
		blindMap(diff);
	}

	// 맵을 출력한다. (확인 용으로 사용되었다.)
	private void printMap(int[] map) {
		int x = 0, y = 0;

		System.out.print("+======================+\n");

		for (y = 0; y < totalHeightCell; y++) {
			System.out.print("|");
			for (x = 0; x < totalWidthCell; x++) {
				System.out.printf("%d ", map[x + y * totalHeightCell]);

				if (((x + 1) % heightCell) == 0)
					System.out.print("| ");
			}

			System.out.print("\n");

			if (((y + 1) % heightCell) == 0)
				System.out.print("+----------------------+\n");
		}
	}

	// Original Map 출력
	public void printOriginalMap() {
		printMap(originalMap);
	}

	// Blind Map 출력
	public void printBlindMap() {
		printMap(blindMap);
	}

	// 오류 검사 3단계를 수행하여 입력한 숫자를 검사
	private boolean checkValidNumber(int[] map, int inIndex, int inNumber) {
		int index = 0, offset = 0;
		int i = 0;

		int curNum = 0;
		int step = 0;

		boolean[] seqNumFlag = new boolean[9];

		assert (index < totalCell);
		assert (inNumber <= blockCell);

		// 맵에 검사할 숫자를 대입
		
		map[inIndex] = inNumber;

		// 3단계에 걸처 검사를 한다.
		for (step = 0; step < 3; step++) {
			// 초기화
			for (i = 0; i < blockCell; i++)
				seqNumFlag[i] = false;

			// Offset 계산(속도 향상을 위해)
			if (step == 0) // 3x3 Box 검사
				offset = (inIndex / (totalWidthCell * heightCell)) * (totalWidthCell * heightCell)
						+ ((inIndex % totalWidthCell) / widthCell) * widthCell;
			else if (step == 1) // 가로 줄 검사
				offset = (inIndex / totalWidthCell) * totalWidthCell;
			else if (step == 2) // 세로 줄 검사
				offset = (inIndex % totalHeightCell);

			for (i = 0; i < 9; i++) {
				// index 값 계산
				if (step == 0) // 3x3 Box 검사
					index = offset + (i % widthCell) + (i / widthCell) * totalWidthCell;
				else if (step == 1) // 가로 줄 검사
					index = offset + i;
				else if (step == 2) // 세로 줄 검사
					index = offset + i * totalWidthCell;

				assert (index < totalCell);

				curNum = map[index];

				// 빈칸 일 경우
				if (curNum == 0)
					continue;

				// SeqNumFlag 배열에 중복됬는지 해당 숫자를 체크한다.
				if (seqNumFlag[curNum - 1] == false)
					seqNumFlag[curNum - 1] = true;
				else
					break;
			}

			if (i < 9)
				return false;
		}

		return true;
	}

	// 오류 검사 3단계를 수행하여 입력한 숫자를 검사
	public boolean checkValidNumber(int index, int number) {
		return checkValidNumber(blindMap, index, number);
	}

	// 맵 생성기
	public boolean makeOriginalMap() {
		int index = 0;
		int ranNum = 0;

		int[] numArray = new int[blockCell];

		// 풀이
		MapQueData[] mapQueData = new MapQueData[queSize];

		int head = 0;
		int i = 0;
		int totalRunCount = 0;
		int data = 0;

		// Map 을 위한 Queue Buffer 할당
		for (i = 0; i < queSize; i++)
			mapQueData[i] = new MapQueData();

		do {
			// 이미 맵에 데이터가 있다면 통과
			if (originalMap[index] != 0) {
				index++;
				continue;
			}

			for (i = 1; i <= blockCell; i++)
				numArray[i - 1] = i;

			// Push Index
			for (i = 1; i <= blockCell; i++) {
				// 1~9 중 랜덤으로 중복을 막기 위해
				// Table 을 사용하여 9번 랜덤을 실행하면 항상 1~9가 나오도록 함.
				ranNum = (int) (Math.random() * (blockCell + 1 - i));
				data = numArray[ranNum];
				numArray[ranNum] = numArray[blockCell - i];

				// 숫자가 오류나지 않는다면 Queue 에 Push 한다.
				if (checkValidNumber(originalMap, index, data) == true) {
					mapQueData[head].index = index;
					mapQueData[head].number = data;

					// 현재 맵을 저장
					System.arraycopy(originalMap, 0, mapQueData[head].map, 0, totalCell);

					head++;

					if (head >= queSize)
						return false;
				}

				totalRunCount++;
			}

			// Pop Index
			head--;

			// Pop 하여 얻은 맵의 정보와 데이터 설정
			System.arraycopy(mapQueData[head].map, 0, originalMap, 0, totalCell);

			index = mapQueData[head].index;
			originalMap[index] = mapQueData[head].number;

			index++;
			if (index >= totalCell)
				return false;

			totalRunCount++;
		} while (head > 0);

		System.out.printf("END (%d,%d) %d\nRunCount:%d\n", index % widthCell, index / heightCell, head, totalRunCount);

		return true;
	}

	// 문제 출제하기 위해 Level 에 따라 맵의 숫자를 감춤
	public void blindMap(int level) {
		int i = 0;
		int leftBlind = levelBlindCount[level];

		assert (leftBlind > 0);

		System.arraycopy(originalMap, 0, blindMap, 0, totalCell);

		while (leftBlind-- > 0) {
			i = (int) (Math.random() * totalCell);

			if (blindMap[i] == 0)
				continue;

			blindMap[i] = 0;
		}
	}

	// Original 맵 데이터 얻기
	public int[] getOriginalMap() {
		int[] map = (int[]) originalMap.clone();

		return map;
	}

	// blind 맵 데이터 얻기
	public int[] getBlindMap() {
		int[] map = (int[]) blindMap.clone();

		return map;
	}

	// blind 맵에 숫자 입력하기
	public boolean setDataBlindMap(int index, int number) {
		if (blindMap[index] != 0)
			return false;

		blindMap[index] = number;

		return true;
	}

	// 자동으로 문제 생성 및 Blind
	public void autoMakeMap(int level) {
		// 맵 초기화
		initMap();

		// 맵 생성
		makeOriginalMap();

		// Blind 맵 생성(3번째 인자는 난이도)
		blindMap(level);
	}
}