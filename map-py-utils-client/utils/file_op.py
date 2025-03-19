from typing import IO, Dict, Union, Optional, Tuple

from lxml import etree
import refile
from megfile import SmartPath

from .md5 import get_str_md5


SourceType = Union[str, IO]
SourcesType = Union[Dict[str, SourceType], Dict[str, str]]


def smart_read(source: SourceType) -> Union[None, str, bytes, bytearray]:
    """smartly read map file, suprort local file, s3 file, and file-like object
    Args:
        source (SourceType): map file path or file-like object

    Returns:
        Union[None, str, bytes, bytearray]: map str data
    """
    str_data = None

    if isinstance(source, str):
        file_path = SmartPath(source)
        if refile.smart_exists(file_path):
            file = refile.smart_open(SmartPath(source), mode="rb")
            str_data = file.read()
            file.close()

    elif hasattr(source, "read"):
        str_data = source.read()  # type: ignore

    return str_data


def load_xml(s3_path: str) -> Optional[Tuple[etree._Element, str]]:
    xml_str = smart_read(s3_path)

    if xml_str is None:
        return

    return etree.fromstring(xml_str), get_str_md5(xml_str)
