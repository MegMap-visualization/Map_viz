from dataclasses import dataclass
from enum import Enum, auto
from typing import List, Dict, Union, Optional

from flask import jsonify, Response


class BufferType(Enum):
    ALL_ROUTING = auto()
    ROUTING = auto()
    ParserResult = auto()


@dataclass
class ResponseData:
    code: int
    status: str
    message: str
    data: Optional[Union[List, Dict]]

    @property
    def json(self) -> Response:
        return jsonify(self.__dict__)
